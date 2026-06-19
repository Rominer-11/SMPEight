package me.rominer_11;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageControl implements Listener 
{
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Player)
		{
			// Multiplies damage by aggregate value of health and hunger, where full hunger and health is 100%
			Player player = (Player) event.getDamager();
			event.setDamage(event.getDamage() * ((double) (player.getFoodLevel() + player.getHealth()) / 40));
		}
	}
}
