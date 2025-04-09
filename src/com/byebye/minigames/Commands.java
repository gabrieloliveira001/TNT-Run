package com.byebye.minigames;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();

		if (command.getName().equalsIgnoreCase("savearea")) {

			if (!(sender instanceof Player)) {
				sender.sendMessage("§cApenas jogadores podem usar esse comando!");
				return true;
			}

			if (args.length < 1) {
				player.sendMessage("§cUso: /savearea <nome>");
				return true;
			}

			String areaName = args[0];

			if (!Main.selection1.containsKey(uuid) || !Main.selection2.containsKey(uuid)) {
				player.sendMessage("§cVocê precisa selecionar os dois pontos antes de salvar a área!");
				return true;
			}

			Location pos1 = Main.selection1.get(uuid);
			Location pos2 = Main.selection2.get(uuid);

			SchematicManager.saveSchematic(player, areaName, pos1, pos2);

			player.sendMessage("§dÁrea §e" + areaName + " §dsalva com sucesso!");
		}

		if (command.getName().equalsIgnoreCase("loadarea")) {

			if (args.length < 1) {
				player.sendMessage("§cUso: /loadarea <nome> [local]");
				return true;
			}

			String areaName = args[0];
			Location loc;

			if (args.length >= 2) {
				String locationName = args[1];
				File locationsFile = new File(Main.m.getDataFolder(), "locations.yml");
				FileConfiguration locationsConfig = YamlConfiguration.loadConfiguration(locationsFile);

				String basePath = "locations." + locationName;
				if (!locationsConfig.contains(basePath)) {
					player.sendMessage("§cLocal §e" + locationName + " §cnão encontrado!");
					return true;
				}

				String worldName = locationsConfig.getString(basePath + ".world");
				double x = locationsConfig.getDouble(basePath + ".x");
				double y = locationsConfig.getDouble(basePath + ".y");
				double z = locationsConfig.getDouble(basePath + ".z");
				float yaw = (float) locationsConfig.getDouble(basePath + ".yaw");
				float pitch = (float) locationsConfig.getDouble(basePath + ".pitch");

				loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
			} else {
				loc = player.getLocation();
			}

			File schematicsFile = new File(Main.m.getDataFolder(), "schematics.yml");
			FileConfiguration schematicsConfig = YamlConfiguration.loadConfiguration(schematicsFile);
			if (!schematicsConfig.contains("schematics." + areaName)) {
				player.sendMessage("§cÁrea §e" + areaName + " §cnão foi encontrada!");
				return true;
			}

			SchematicManager.loadSchematic(areaName, loc);
			player.sendMessage("§dÁrea §e" + areaName + " §dcarregada com sucesso!");
			return true;
		}

		if (command.getName().equalsIgnoreCase("setloc")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cApenas jogadores podem usar este comando.");
				return true;
			}

			if (args.length < 1) {
				player.sendMessage("§cUso: /setloc <nome>");
				return true;
			}

			String locName = args[0];
			Location loc = player.getLocation();

			File locationsFile = new File(Main.m.getDataFolder(), "locations.yml");
			FileConfiguration locationsConfig = YamlConfiguration.loadConfiguration(locationsFile);

			String basePath = "locations." + locName;
			locationsConfig.set(basePath + ".world", loc.getWorld().getName());
			locationsConfig.set(basePath + ".x", loc.getX());
			locationsConfig.set(basePath + ".y", loc.getY());
			locationsConfig.set(basePath + ".z", loc.getZ());
			locationsConfig.set(basePath + ".yaw", loc.getYaw());
			locationsConfig.set(basePath + ".pitch", loc.getPitch());

			try {
				locationsConfig.save(locationsFile);
				player.sendMessage("§aLocal §e" + locName + " §asalvo com sucesso!");
			} catch (IOException e) {
				player.sendMessage("§cErro ao salvar local!");
				e.printStackTrace();
			}

			return true;
		}

		if (command.getName().equalsIgnoreCase("tntrun")) {
			if (args.length < 1) {
				return true;
			}
			if (args[0].equalsIgnoreCase("start")) {
				if (tntrun.gamestarting) {
					player.sendMessage("§cO minigame já está em andamento.");
					return true;
				} else {
					tntrun.startgame();
				}
			} else {
				player.sendMessage("§cUse /tntrun start");
			}
		}
		return true;
	}
}
