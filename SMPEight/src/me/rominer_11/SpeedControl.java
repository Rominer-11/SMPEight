package me.rominer_11;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SpeedControl implements Listener
{
	public SpeedControl(JavaPlugin plugin)
	{
		// Run every 10 ticks (0.5 seconds) instead of on every PlayerMoveEvent
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for (Player player : plugin.getServer().getOnlinePlayers())
				{
					updateSpeed(player);
				}
			}
		}.runTaskTimer(plugin, 0L, 10L);
	}

	private void updateSpeed(Player player)
	{
		// Health and hunger contribute to player max speed
		float newspeed = (float) (0.0025 * (((0.5) * player.getFoodLevel()) + ((1.5) * player.getHealth())) + 0.1);
		
		// Armor contributes as well!
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();

		float armordebuff = (float) 0.0;

		if (helmet != null && helmet.getType() != Material.LEATHER_HELMET && helmet.getType() != Material.CHAINMAIL_HELMET)
		{
			armordebuff += 0.1125;
		}
		// Exclude Elytra
		if (chestplate != null && chestplate.getType() != Material.LEATHER_CHESTPLATE
				&& chestplate.getType() != Material.CHAINMAIL_CHESTPLATE
				&& chestplate.getType() != Material.ELYTRA)
		{
			armordebuff += 0.3;
		}
		if (leggings != null && leggings.getType() != Material.LEATHER_LEGGINGS && leggings.getType() != Material.CHAINMAIL_LEGGINGS)
		{
			armordebuff += 0.225;
		}
		if (boots != null && boots.getType() != Material.LEATHER_BOOTS && boots.getType() != Material.CHAINMAIL_BOOTS)
		{
			armordebuff += 0.1125;
		}

		newspeed = newspeed - ((armordebuff * (float) 0.1));

		// Clamp speed 
		newspeed = Math.max(0.0f, Math.min(1.0f, newspeed));

		// Set new speed
		player.setWalkSpeed(newspeed);
	}
}