package me.rominer_11;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements org.bukkit.command.CommandExecutor
{
	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new SpeedControl(this), this);
		getServer().getPluginManager().registerEvents(new DamageControl(), this);
		getServer().getPluginManager().registerEvents(new GravityControl(this), this);
		getServer().getPluginManager().registerEvents(new ClimateControl(this), this);
		getServer().getPluginManager().registerEvents(new DamageControl(), this);
		getServer().getPluginManager().registerEvents(new MobControl(this), this);
		getServer().getPluginManager().registerEvents(new BlockControl(), this);
		getServer().getPluginManager().registerEvents(new EnchantControl(), this);
		getServer().getPluginManager().registerEvents(new BedControl(), this);
		getServer().getPluginManager().registerEvents(new InfoBook(), this);
		getCommand("smpguide").setExecutor(this);
		System.out.println("SMPEight enabled!");
		System.out.println("SMPEight is best experienced on HARD difficulty, with keepInventory FALSE, and with playersSleepingPercentage 50");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (command.getName().equalsIgnoreCase("smpguide"))
		{
			if (sender instanceof Player player)
			{
				player.openBook(InfoBook.createBook());
				return true;
			}
			sender.sendMessage("Only players can use this command.");
			return true;
		}
		return false;
	}

	@Override
	public void onDisable()
	{
		System.out.println("SMPEight disabled.");
	}
}
