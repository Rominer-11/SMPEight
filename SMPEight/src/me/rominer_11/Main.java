package me.rominer_11;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(new SpeedControl(), this);
		getServer().getPluginManager().registerEvents(new ClimateControl(), this);
		getServer().getPluginManager().registerEvents(new DamageControl(), this);
		getServer().getPluginManager().registerEvents(new MobControl(), this);
		System.out.println("SMPEight enabled!");
		System.out.println("SMPEight is best experienced on HARD difficulty.");
	}
	@Override
	public void onDisable()
	{
		System.out.println("SMPEight disabled.");
	}
}
