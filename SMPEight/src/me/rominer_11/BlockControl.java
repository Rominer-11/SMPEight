package me.rominer_11;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockControl implements Listener 
{
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		// Increase exhaustion on block break
		Player player = event.getPlayer();
		player.setExhaustion((float) (player.getExhaustion() + 0.1));
	}
}
