package me.rominer_11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;

public class MobControl implements Listener 
{	
	private static Main plugin;
	private final Map<UUID, BukkitTask> creeperTasks = new HashMap<>();
	private final List<UUID> explosionBlockDeath = new ArrayList<>();
	
	public MobControl(Main plugin) 
	{
		MobControl.plugin = plugin;
	}
	
	@EventHandler
	public void spawnEvent(EntitySpawnEvent event)
	{
		if (event.getEntity() instanceof Creeper)
		{
			// Power all creepers
			Creeper creeper = (Creeper) event.getEntity();
			creeper.setPowered(true);
			
			// Creeper ai code
			BukkitTask task = new BukkitRunnable() {
				@Override
				public void run()
				{
					if (creeper.getTarget() instanceof Player)
					{
						Player player = (Player) creeper.getTarget();
						double distance = creeper.getLocation().distance(player.getLocation());
						if (!creeper.hasLineOfSight(player) && distance < 6.0)
						{
							creeper.ignite();
						}
						
						new BukkitRunnable() {
							@Override
							public void run()
							{
								double newdistance = creeper.getLocation().distance(player.getLocation());
								if (creeper.getTarget() != player && newdistance < 6.0)
								{
									creeper.ignite();
								}
								if (Math.abs(player.getLocation().getBlockY() - creeper.getLocation().getBlockY()) > 2.0 &&
										Math.sqrt(Math.pow(player.getLocation().getBlockX() - creeper.getLocation().getBlockX(), 2) + Math.pow(player.getLocation().getBlockZ() - creeper.getLocation().getBlockZ(), 2)) < 4)
								{
									if (Math.abs(distance - newdistance) < 2.0 && creeper.getTarget() == player)
									{
										creeper.ignite();
									}
								}
							}
						}.runTaskLater(plugin, 20L);
					}
				}
			}.runTaskTimer(plugin, 0L, 20L);
			creeperTasks.put(creeper.getUniqueId(), task);
		}
	}
	@EventHandler
	/**
	 * Thank you to this guy: https://www.spigotmc.org/threads/tutorial-calculating-damage-taken-by-a-player-manually.424680/
	 * @param event
	 */
	public void onEntityDamageEvent(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION)
			{
				if (player.isBlocking())
				{
					// Calculates damage
					double damage = event.getDamage() / 2;
					double armorPoints = player.getAttribute(Attribute.ARMOR).getValue();
					double armorToughness = player.getAttribute(Attribute.ARMOR_TOUGHNESS).getValue();
					double withArmorReduction = damage * (1 - Math.min(20, Math.max(armorPoints / 5, armorPoints - damage / (2 + armorToughness / 4))) / 25);
					PotionEffect res = player.getPotionEffect(PotionEffectType.RESISTANCE);
					int resistanceLevel = res == null ? 0 : res.getAmplifier();
					double withResistance = withArmorReduction * (1 - (resistanceLevel * 0.2));
					PlayerInventory inv = player.getInventory();
				    ItemStack helm = inv.getHelmet();
				    ItemStack chest = inv.getChestplate();
				    ItemStack legs = inv.getLeggings();
				    ItemStack boot = inv.getBoots();
				    double epf = 2 * (helm != null ? helm.getEnchantmentLevel(Enchantment.BLAST_PROTECTION) : 0) +
			            2 * (chest != null ? chest.getEnchantmentLevel(Enchantment.BLAST_PROTECTION) : 0) +
			            2 * (legs != null ? legs.getEnchantmentLevel(Enchantment.BLAST_PROTECTION) : 0) +
			            2 * (boot != null ? boot.getEnchantmentLevel(Enchantment.BLAST_PROTECTION) : 0) +
					    (helm != null ? helm.getEnchantmentLevel(Enchantment.PROTECTION) : 0) +
			            (chest != null ? chest.getEnchantmentLevel(Enchantment.PROTECTION) : 0) +
			            (legs != null ? legs.getEnchantmentLevel(Enchantment.PROTECTION) : 0) +
			            (boot != null ? boot.getEnchantmentLevel(Enchantment.PROTECTION) : 0);
				    double withEnchantmentReduction = withResistance * (1 - (Math.min(20.0, epf) / 25));
				    				    
				    // Applies damage
				    event.setDamage(0);
				    if (Math.max(0.50 * (player.getHealth() - withEnchantmentReduction), 0.0) == 0.0)
				    {
				    	explosionBlockDeath.add(player.getUniqueId());
				    }
				    player.setHealth(Math.max(0.50 * (player.getHealth() - withEnchantmentReduction), 0.0));
				}
			}
		}
	}
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			if (event.getDamager() instanceof Monster)
			{
				event.setDamage(event.getDamage() * 2.5);
			}
		}
		if (event.getEntity() instanceof Monster)
		{
			if (event.getDamager() instanceof Creeper)
			{
				event.setDamage(event.getDamage() / 8);
			}
		}
	}
	
	
	// Custom death message handling
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		// Places skull upon death
		if (event.getEntity().getLocation().getBlock().getType() == Material.AIR)
		{
			event.getEntity().getLocation().getBlock().setType(Material.SKELETON_SKULL);
		}
		if (explosionBlockDeath.contains(event.getEntity().getUniqueId()))
		{
			event.setDeathMessage(event.getEntity().getDisplayName() + " forgot that blocking doesn't stop explosions.");
			explosionBlockDeath.remove(event.getEntity().getUniqueId());
		}
	}
	
	// Creeper handling methods
	@EventHandler
	public void onCreeperDeath(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Creeper)
		{
			BukkitTask task = creeperTasks.remove(event.getEntity().getUniqueId());
			if (task != null)
			{
				task.cancel();
			}
		}
	}
	@EventHandler
	public void onCreeperUnload(ChunkUnloadEvent event)
	{
		for (Entity entity : event.getChunk().getEntities())
		{
			if (entity instanceof Creeper)
			{
				BukkitTask task = creeperTasks.remove(entity.getUniqueId());
				if (task != null)
				{
					task.cancel();
				}
			}
		}
	}
	@EventHandler
	public void onCreeperIgnite(ExplosionPrimeEvent event)
	{
		if (event.getEntity() instanceof Creeper)
		{
			BukkitTask task = creeperTasks.remove(event.getEntity().getUniqueId());
			if (task != null)
			{
				task.cancel();
			}
		}
	}
}
