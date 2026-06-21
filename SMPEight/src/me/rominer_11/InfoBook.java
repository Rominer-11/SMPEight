package me.rominer_11;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class InfoBook implements Listener
{
	public static ItemStack createBook()
	{
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();

		meta.setTitle("SMPEight Guide");
		meta.setAuthor("ghoststaryt");

		meta.setPages(
			// Page 1: Welcome
			"§l§nWelcome to SMPEight!§r\n\n" +
			"This plugin makes vanilla Minecraft's progression longer and more challenging. " +
			"This book explains the key features in broad terms.\n\n" +
			"Use §l/smpguide§r to reopen this book at any time.",

			// Page 2: Weakness
			"§l§nWeakness§r\n\n" +
			"Depending on how §lhealthy§r you are, some of your in-game stats may drop.\n\n" +
			"A good idea might be to avoid combat when you are in poor condition.\n\n",
			
			// Page 3: Climate
			"§l§nClimate§r\n\n" +
			"Certain biomes may be significantly less hospitable than others.\n\n" +
			"Use §lLeather armor§r to stay warm, and remember that §lChainmail§r is a breathable material.\n\n",

			// Page 4: The Nether
			"§l§nThe Nether§r\n\n" +
			"The §lNether§r is §lVERY§r hot!\n\n" +
			"Navigating the Nether will be difficult. It is, however, entirely possible with a little bit of strategy. Don't give up!\n\n",

			// Page 5: Mob Control
			"§l§nMobs§r\n\n" +
			"Monsters will be very tough to deal with.\n\n" +
			"Don't get discouraged!\n\n" +
			"If you plan your base well, they won't be a regular issue.\n\n",

			// Page 6: Credits
			"§l§nCredits§r\n\n" +
			"Lead Developer: \n August - rominer_11\n" +
			"Developer: \n Sky - ghoststaryt"
		);
		book.setItemMeta(meta);
		return book;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore())
		{
			// Delay by 1 tick to ensure the player is fully connected
			player.getServer().getScheduler().runTask(
				player.getServer().getPluginManager().getPlugin("SMPEight"),
				() -> player.openBook(createBook())
			);
			ItemStack steak = new ItemStack(Material.COOKED_BEEF, 16);
			player.getInventory().addItem(steak);
		}
	}
}