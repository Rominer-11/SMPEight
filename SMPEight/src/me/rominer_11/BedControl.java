
package me.rominer_11;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class BedControl implements Listener 
{
	@EventHandler
	public void onSleep(PlayerBedEnterEvent event)
	{
		Player player = event.getPlayer();
		if (player.getLocation().getWorld().getEnvironment() == World.Environment.NETHER ||
				player.getLocation().getWorld().getEnvironment() == World.Environment.THE_END)
		{
			player.teleport(event.getBed().getLocation());
			player.sendMessage("Bed-bombing doesn't work here!");
			Interaction interaction = (Interaction) player.getWorld().spawnEntity(player.getLocation(), EntityType.INTERACTION);
			player.getWorld().createExplosion(player.getLocation(), 10F, true, true, interaction);
			interaction.remove();
			
			event.setCancelled(true);
		}
	}
}
