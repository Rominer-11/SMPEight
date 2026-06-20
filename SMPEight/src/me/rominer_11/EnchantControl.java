package me.rominer_11;

import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class EnchantControl implements Listener 
{
	@EventHandler
	public void onEnchantTableOpen(PrepareItemEnchantEvent event)
	{
		EnchantmentOffer[] offers = event.getOffers();
		for (EnchantmentOffer offer : offers)
		{
			if (offer != null)
			{
				offer.setCost((int) (offer.getCost() * 1.5));
			}
		}
	}
	
	@EventHandler
	public void onAnvilOpen(PrepareAnvilEvent event)
	{
		AnvilInventory inv = event.getInventory();
		
		ItemStack left = inv.getItem(0);
		ItemStack right = inv.getItem(1);
		
		if ((left != null && !left.getType().isAir()) && (right != null && !right.getType().isAir()))
		{
			if ((!left.getEnchantments().isEmpty() || left.getType() == Material.ENCHANTED_BOOK) && (!right.getEnchantments().isEmpty() || right.getType() == Material.ENCHANTED_BOOK))
			{
				event.setResult(null);
			}
		}
	}
}
