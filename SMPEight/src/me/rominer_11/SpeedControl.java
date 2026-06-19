package me.rominer_11;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SpeedControl implements Listener 
{
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		
		// Health and hunger contribute to player max speed
		float newspeed = (float) (0.0025 * (player.getFoodLevel() + player.getHealth()) + 0.1);
		
		// Armor contributes as well!
		ItemStack helmet = player.getInventory().getHelmet();
		ItemStack chestplate = player.getInventory().getChestplate();
		ItemStack leggings = player.getInventory().getLeggings();
		ItemStack boots = player.getInventory().getBoots();
		
		float armordebuff = (float) 0.0;
		
		if (helmet != null && helmet.getType() != Material.LEATHER_HELMET && helmet.getType() != Material.CHAINMAIL_HELMET)
		{
			armordebuff += .15;
		}
		if (chestplate != null && chestplate.getType() != Material.LEATHER_CHESTPLATE && chestplate.getType() != Material.CHAINMAIL_CHESTPLATE)
		{
			armordebuff += .40;
		}
		if (leggings != null && leggings.getType() != Material.LEATHER_LEGGINGS && leggings.getType() != Material.CHAINMAIL_LEGGINGS)
		{
			armordebuff += .30;
		}
		if (boots != null && boots.getType() != Material.LEATHER_BOOTS && boots.getType() != Material.CHAINMAIL_BOOTS)
		{
			armordebuff += .15;
		}
		
		newspeed = newspeed - ((armordebuff * (float) 0.1));

		
		// Set new speed
		player.setWalkSpeed(newspeed);
	}
}
