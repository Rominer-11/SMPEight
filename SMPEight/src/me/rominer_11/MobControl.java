package me.rominer_11;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MobControl implements Listener 
{
	@EventHandler
	public void spawnEvent(EntitySpawnEvent event)
	{
		if (event.getEntity() instanceof Creeper)
		{
			Creeper creeper = (Creeper) event.getEntity();
			creeper.setPowered(true);
		}
	}
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if (event.getCause() == DamageCause.BLOCK_EXPLOSION || event.getCause() == DamageCause.ENTITY_EXPLOSION)
			{
				if (player.isBlocking())
				{
					player.damage(event.getDamage());
					event.setDamage(0);
				}
			}
		}
	}
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			if (event.getDamager() instanceof Monster)
			{
				event.setDamage(event.getDamage() * 2);
			}
		}
	}
}
