package me.rominer_11;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
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
		System.out.println("SMPEight enabled!");
		System.out.println("SMPEight is best experienced on HARD difficulty, with keepInventory FALSE, and with playersSleepingPercentage 50");
	}
	@Override
	public void onDisable()
	{
		System.out.println("SMPEight disabled.");
	}
}
