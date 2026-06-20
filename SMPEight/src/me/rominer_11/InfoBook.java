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
		meta.setAuthor("rominer_11");

		meta.setPages(
			// Page 1: Welcome
			"§l§nWelcome to SMPEight!§r\n\n" +
			"This server changes vanilla Minecraft in many ways. " +
			"This book explains the key features.\n\n" +
			"Use §l/smpguide§r to reopen this book at any time.",

			// Page 2: Speed Control
			"§l§nSpeed Control§r\n\n" +
			"Your walk speed is based on your §lhealth§r and §lhunger§r. " +
			"The fuller your bars, the faster you move.\n\n",
			
			// Page 3: Speed control continued
			"§lArmor§r slows you down! Heavy armor like diamond and netherite has the biggest debuff. " +
			"§lLeather§r, §lchainmail§r, and §lelytra§r are lightweight and have no speed penalty.",

			// Page 4: Damage Control
			"§l§nDamage Control§r\n\n" +
			"The damage you deal scales with your §lhealth§r and §lhunger§r. " +
			"Full health and hunger means 100% damage.\n\n" +
			"§lMonsters§r deal §l2.5x§r damage to you, so stay prepared!",

			// Page 5: Climate Control
			"§l§nClimate§r\n\n" +
			"Biome §ltemperature§r and §lhumidity§r affect your comfort. " +
			"§lLeather armor§r adds warmth. " +
			"§lChainmail§r is climate-neutral.\n\n",

			// Page 6: climate control continued
			"The §lNether§r is extremely hot! Like, §lVERY§r hot!\n " +
			"Hunger drains faster in uncomfortable conditions.",

			// Page 7: Mob Control
			"§l§nMobs§r\n\n" +
			"§lCreepers§r have smarter AI! " +
			"§lShields§r reduce explosion damage but don't block it completely.\n\n" +
			"Again, Monsters deal §l2.5x§r damage to you, so stay prepared!",

			// Page 8: Enchanting & Anvils
			"§l§nEnchanting & Anvils§r\n\n" +
			"Enchanting table costs are §l1.5x§r more expensive.\n\n" +
			"Anvils §lcannot§r combine two items if either has enchantments. " +
			"You can still repair items by combining them.",

			// Page 9: Mining & Beds
			"§l§nMining & Beds§r\n\n" +
			"Breaking blocks increases your §lhunger exhaustion§r.\n\n" +
			"Whatever you do, §lPLEASE§r don't try bed-bombing in the Nether or End!",

			// Page 10: Tips
			"§l§nTips§r\n\n" +
			"• Wear §lleather§r or §lchainmail§r to stay fast.\n\n" +
			"• Match your armor to the §lclimate§r.\n\n" +
			"• §l/smpguide§r to reopen this book"
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
		}
	}
}