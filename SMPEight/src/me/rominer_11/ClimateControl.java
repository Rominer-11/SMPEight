package me.rominer_11;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ClimateControl implements Listener 
{
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
		
		if (helmet != null && helmet.getType() != Material.CHAINMAIL_HELMET)
		{
			armordebuff += .15;
		}
		if (chestplate != null && chestplate.getType() != Material.CHAINMAIL_CHESTPLATE)
		{
			armordebuff += .40;
		}
		if (leggings != null && leggings.getType() != Material.CHAINMAIL_LEGGINGS)
		{
			armordebuff += .30;
		}
		if (boots != null && boots.getType() != Material.CHAINMAIL_BOOTS)
		{
			armordebuff += .15;
		}
		temperature += 0.4 * armordebuff;
		
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
			player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 1));
		}
		
		if (player.getLocation().getWorld().getEnvironment() == World.Environment.NETHER)
		{
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("This dimension is very hot!"));
			if (player.getFireTicks() <= 0)
			{
				player.setFireTicks(32000);
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
