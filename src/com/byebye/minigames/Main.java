package com.byebye.minigames;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {

	public static Map<UUID, Location> selection1 = new HashMap<>();
	public static Map<UUID, Location> selection2 = new HashMap<>();

	public static Main m;
	public static FileConfiguration cf;
	public static BukkitScheduler sh;
	public static PluginManager pm;

	@Override
	public void onLoad() {
		m = this;
		cf = getConfig();
		sh = Bukkit.getScheduler();
		pm = Bukkit.getPluginManager();

	}

	@Override
	public void onEnable() {
		pm.registerEvents(new Events(), this);
		pm.registerEvents(new tntrun(), this);
		m.getCommand("savearea").setExecutor(new Commands());
		m.getCommand("setloc").setExecutor(new Commands());
		m.getCommand("loadarea").setExecutor(new Commands());
		m.getCommand("tntrun").setExecutor(new Commands());
		saveDefaultConfig();
	}

	@Override
	public void onDisable() {

		HandlerList.unregisterAll(new Events());

	}
}
