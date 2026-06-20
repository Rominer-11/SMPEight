package me.rominer_11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class EnchantControl implements Listener 
{
	private final Map<UUID, EnchantmentOffer[]> playerEnchantPreserver = new HashMap<>();
	
	@EventHandler
	public void onEnchantTableOpen(PrepareItemEnchantEvent event)
	{
		EnchantmentOffer[] offers = event.getOffers();
		playerEnchantPreserver.put(event.getEnchanter().getUniqueId(), offers);
		for (EnchantmentOffer offer : offers)
		{
			if (offer != null)
			{
				offer.setCost((int) (offer.getCost() * 1.5));
			}
		}
	}
	@EventHandler
	public void onEnchant(EnchantItemEvent event)
	{
		// Add the intended enchant displayed at offer screen
		Player player = event.getEnchanter();		
		ItemStack item = event.getItem();
		int button = event.whichButton();
		EnchantmentOffer[] offers = playerEnchantPreserver.remove(player.getUniqueId());
		EnchantmentOffer offer = offers[button];
		item.addEnchantment(offer.getEnchantment(), offer.getEnchantmentLevel());
		
		// Remove conflicting enchants
		Map<Enchantment, Integer> otherEnchants = event.getEnchantsToAdd();
		List<Enchantment> toRemove = new ArrayList<>();
		for (Map.Entry<Enchantment, Integer> enchantLevelPair : otherEnchants.entrySet())
		{
			if (enchantLevelPair.getKey().conflictsWith(offer.getEnchantment()) || enchantLevelPair.getKey().equals(offer.getEnchantment()))
			{
				toRemove.add(enchantLevelPair.getKey());
			}
		}
		for (Enchantment enchant : toRemove)
		{
			otherEnchants.remove(enchant);
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
