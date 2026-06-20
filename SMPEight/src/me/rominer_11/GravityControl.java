package me.rominer_11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GravityControl implements Listener
{
	private double time = 0;
	// Full cycle = 60 seconds (600 ticks): 15s above 50% gravity, 15s below 50% gravity
	// 2*PI / 600 ticks = one full cosine cycle over 30 seconds
	private static final double TIME_INCREMENT = 2.0 * Math.PI / 600.0;
	// Minecraft's default gravitational acceleration per tick
	private static final double DEFAULT_GRAVITY = 0.08;
	// Gravity applied after leaving the End
	private static final double EXIT_GRAVITY = 0.025;
	// How long the exit gravity lasts (3 minutes in ticks)
	private static final long EXIT_DURATION_TICKS = 3600L;

	// Tracks which players are currently in the End
	private final Map<UUID, Boolean> inEnd = new HashMap<>();
	// Tracks pending gravity-reset tasks so they can be cancelled if player re-enters the End
	private final Map<UUID, BukkitRunnable> pendingResets = new HashMap<>();

	public GravityControl(JavaPlugin plugin)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				time += TIME_INCREMENT;
				if (time > 2.0 * Math.PI)
				{
					time -= 2.0 * Math.PI;
				}

				// (cos(x) + 1) / 2: ranges from 0.0 (no gravity) to 1.0 (full gravity)
				double gravityMultiplier = (Math.cos(time) + 1.0) / 2.0;

				for (World world : Bukkit.getWorlds())
				{
					if (world.getEnvironment() != World.Environment.THE_END)
						continue;

					// Players in the End: apply fluctuating gravity and track presence
					for (Player player : world.getPlayers())
					{
						UUID uuid = player.getUniqueId();

						// If player just re-entered the End, cancel any pending reset
						if (pendingResets.containsKey(uuid))
						{
							pendingResets.get(uuid).cancel();
							pendingResets.remove(uuid);
						}

						inEnd.put(uuid, true);

						player.getAttribute(Attribute.GRAVITY)
							.setBaseValue(DEFAULT_GRAVITY * gravityMultiplier);
					}

					// Items in the End: scale downward velocity
					for (Item item : world.getEntitiesByClass(Item.class))
					{
						Vector vel = item.getVelocity();
						if (vel.getY() < 0)
						{
							vel.setY(vel.getY() * gravityMultiplier);
							item.setVelocity(vel);
						}
					}
				}

				// Check for players who left the End
				for (Map.Entry<UUID, Boolean> entry : inEnd.entrySet())
				{
					UUID uuid = entry.getKey();
					Player player = Bukkit.getPlayer(uuid);

					if (player == null || !player.isOnline())
						continue;

					// If player is no longer in the End dimension
					if (player.getWorld().getEnvironment() != World.Environment.THE_END)
					{
						inEnd.remove(uuid);

						// Cancel any existing pending reset for this player
						if (pendingResets.containsKey(uuid))
						{
							pendingResets.get(uuid).cancel();
							pendingResets.remove(uuid);
						}

						// Set flat exit gravity
						player.getAttribute(Attribute.GRAVITY).setBaseValue(EXIT_GRAVITY);

						// Schedule reset back to default after 3 minutes
						BukkitRunnable resetTask = new BukkitRunnable()
						{
							@Override
							public void run()
							{
								if (player.isOnline())
								{
									player.getAttribute(Attribute.GRAVITY)
										.setBaseValue(DEFAULT_GRAVITY);
								}
								pendingResets.remove(uuid);
							}
						};
						resetTask.runTaskLater(plugin, EXIT_DURATION_TICKS);
						pendingResets.put(uuid, resetTask);
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 1L);
	}

	@EventHandler
	public void onFallDamage(EntityDamageEvent event)
	{
		if (!(event.getEntity() instanceof Player player))
			return;

		if (event.getCause() != DamageCause.FALL)
			return;

		double gravity = player.getAttribute(Attribute.GRAVITY).getBaseValue();
		double multiplier = gravity / DEFAULT_GRAVITY;

		event.setDamage(event.getDamage() * multiplier);
	}
}