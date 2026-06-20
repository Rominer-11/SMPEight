package me.rominer_11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.BlockType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * This class implements climate-based features including...
 * Increased hunger based on temperature
 * Heat stroke and hypothermia
 * Nether fire
 */
public class ClimateControl implements Listener 
{
	private static Main plugin;

	private Map<Material, Double> armorWarmth;
	private final Map<UUID, BukkitTask> playersInNether = new HashMap<>();
	private final Map<UUID, BukkitTask> playersInBadClimate = new HashMap<>();
	
	public ClimateControl(Main plugin)
	{
		ClimateControl.plugin = plugin;
		
		this.armorWarmth = new HashMap<>();
		this.armorWarmth.put(Material.LEATHER_HELMET, 0.30);
		this.armorWarmth.put(Material.LEATHER_CHESTPLATE, 0.80);
		this.armorWarmth.put(Material.LEATHER_LEGGINGS, 0.60);
		this.armorWarmth.put(Material.LEATHER_BOOTS, 0.30);
		this.armorWarmth.put(Material.CHAINMAIL_HELMET, 0.0);
		this.armorWarmth.put(Material.CHAINMAIL_CHESTPLATE, 0.0);
		this.armorWarmth.put(Material.CHAINMAIL_LEGGINGS, 0.0);
		this.armorWarmth.put(Material.CHAINMAIL_BOOTS, 0.0);
	}
	
	public double getDiscomfort(Player player)
	{
		double temperature = player.getLocation().getBlock().getTemperature();
		double humidity = player.getLocation().getBlock().getHumidity();
					
		// Armor contributes as well!
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();
		
		double armordebuff = 0.0;
		
		if (helmet != null)
		{
			if (this.armorWarmth.containsKey(helmet.getType()))
			{
				armordebuff += armorWarmth.get(helmet.getType());
			}
			else
			{
				armordebuff += .0375;
			}
		}
		if (chestplate != null)
		{
			if (this.armorWarmth.containsKey(chestplate.getType()))
			{
				armordebuff += armorWarmth.get(chestplate.getType());
			}
			else
			{
				armordebuff += .075;
			}
		}
		if (leggings != null)
		{
			if (this.armorWarmth.containsKey(leggings.getType()))
			{
				armordebuff += armorWarmth.get(leggings.getType());
			}
			else
			{
				armordebuff += .10;
			}
		}
		if (boots != null)
		{
			if (this.armorWarmth.containsKey(boots.getType()))
			{
				armordebuff += armorWarmth.get(boots.getType());
			}
			else
			{
				armordebuff += .0375;
			}
		}
		
		temperature += (0.5) * armordebuff;
		
		double discomfort = (((1.5) * Math.abs(1.0 - temperature)) + ((0.5) * humidity));
		
		//debug info
		//player.sendMessage("D: " + String.format("%.2f", discomfort) + " T: " + String.format("%.2f", temperature) + " H: " + String.format("%.2f", humidity));	

		
		return discomfort;
	}
	
	@EventHandler 
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		double discomfort = getDiscomfort(player);
		if (discomfort >= 2.0)
		{
			if (!playersInBadClimate.containsKey(player.getUniqueId()))
			{
				BukkitTask task = new BukkitRunnable() {
					@Override
					public void run()
					{
						if (player.getFireTicks() <= 0 && player.getLocation().getBlock().getType() != Material.WATER_CAULDRON)
						{
							player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 1));
							player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 1));
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Your clothes are not suitable for this climate!"));
						}
					}
				}.runTaskTimer(plugin, 0L, 20L);
				playersInBadClimate.put(player.getUniqueId(), task);
			}
		}
		else if (playersInBadClimate.containsKey(player.getUniqueId()))
		{
			BukkitTask task = playersInBadClimate.remove(player.getUniqueId());
			if (task != null)
			{
				task.cancel();
			}
		}
		
		if (player.getLocation().getWorld().getEnvironment() == World.Environment.NETHER)
		{
			
			if (!playersInNether.containsKey(player.getUniqueId()))
			{
				BukkitTask task = new BukkitRunnable() {
					@Override
					public void run()
					{
						if (player.getFireTicks() <= 0 && player.getLocation().getBlock().getType() != Material.WATER_CAULDRON)
						{
							player.setFireTicks(32000);
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("This dimension is very hot!"));
						}
					}
				}.runTaskTimer(plugin, 0L, 20L);
				playersInNether.put(player.getUniqueId(), task);
			}
		}
		else if (playersInNether.containsKey(player.getUniqueId()))
		{
			BukkitTask task = playersInNether.remove(player.getUniqueId());
			if (task != null)
			{
				task.cancel();
			}
		}
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			
			double discomfort = getDiscomfort(player);
			
			// Slows or increases hunger depending on discomfort level
			if (discomfort < 1.0 && player.getFoodLevel() != 0)
			{
				player.setSaturation((float) (discomfort * (-2.0) + 2.0));
			}
			else if (discomfort >= 1.0)
			{
				event.setFoodLevel((int) (event.getFoodLevel() - (Math.round(discomfort - 1))));
			}
		}
	}
}
