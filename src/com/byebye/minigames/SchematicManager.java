package com.byebye.minigames;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SchematicManager {
	
	public static void saveSchematicSimples(Player player, String schematicName, Location pos1, Location pos2) {
		int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
		int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
		int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
		int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
		int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
		int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

		File schematicsFile = new File(Main.m.getDataFolder(), "schematics.yml");
		FileConfiguration schematicsConfig = YamlConfiguration.loadConfiguration(schematicsFile);

		String basePath = "schematics." + schematicName;
		schematicsConfig.set(basePath + ".width", maxX - minX + 1);
		schematicsConfig.set(basePath + ".height", maxY - minY + 1);
		schematicsConfig.set(basePath + ".length", maxZ - minZ + 1);

		for (int y = minY; y <= maxY; y++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int x = minX; x <= maxX; x++) {
					Block block = pos1.getWorld().getBlockAt(x, y, z);
					if (block.getType() != Material.AIR) {
						String blockPath = basePath + ".blocks." + (x - minX) + "_" + (y - minY) + "_" + (z - minZ);
						schematicsConfig.set(blockPath + ".type", block.getType().toString());
						schematicsConfig.set(blockPath + ".data", block.getBlockData().getAsString());
					}
				}
			}
		}

		try {
			schematicsConfig.save(schematicsFile);
			player.sendMessage("§aSchematic salva com sucesso!");
		} catch (IOException e) {
			player.sendMessage("§cErro ao salvar schematic!");
			e.printStackTrace();
		}
	}

	public static void saveSchematic(Player player, String schematicName, Location pos1, Location pos2) {
		int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
		int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
		int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
		int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
		int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
		int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

		File schematicsFile = new File(Main.m.getDataFolder(), "schematics.yml");
		FileConfiguration schematicsConfig = YamlConfiguration.loadConfiguration(schematicsFile);

		String basePath = "schematics." + schematicName;
		schematicsConfig.set(basePath + ".width", maxX - minX + 1);
		schematicsConfig.set(basePath + ".height", maxY - minY + 1);
		schematicsConfig.set(basePath + ".length", maxZ - minZ + 1);

		for (int y = minY; y <= maxY; y++) {
			for (int z = minZ; z <= maxZ; z++) {
				for (int x = minX; x <= maxX; x++) {
					Block block = pos1.getWorld().getBlockAt(x, y, z);
					String blockPath = basePath + ".blocks." + (x - minX) + "_" + (y - minY) + "_" + (z - minZ);
					schematicsConfig.set(blockPath + ".type", block.getType().toString());
					schematicsConfig.set(blockPath + ".data", block.getBlockData().getAsString());
				}
			}
		}

		try {
			schematicsConfig.save(schematicsFile);
			player.sendMessage("§aSchematic salva com sucesso!");
		} catch (IOException e) {
			player.sendMessage("§cErro ao salvar schematic!");
			e.printStackTrace();
		}
	}

	public static void loadSchematic(String schematicName, Location startLocation) {
		File schematicsFile = new File(Main.m.getDataFolder(), "schematics.yml");
		FileConfiguration schematicsConfig = YamlConfiguration.loadConfiguration(schematicsFile);

		String basePath = "schematics." + schematicName;
		if (!schematicsConfig.contains(basePath)) {
			return;
		}

		for (String key : schematicsConfig.getConfigurationSection(basePath + ".blocks").getKeys(false)) {
			String[] coords = key.split("_");
			int x = Integer.parseInt(coords[0]) + startLocation.getBlockX();
			int y = Integer.parseInt(coords[1]) + startLocation.getBlockY();
			int z = Integer.parseInt(coords[2]) + startLocation.getBlockZ();
			Material type = Material.valueOf(schematicsConfig.getString(basePath + ".blocks." + key + ".type"));
			String data = schematicsConfig.getString(basePath + ".blocks." + key + ".data");

			Location blockLocation = new Location(startLocation.getWorld(), x, y, z);
			Block block = blockLocation.getBlock();
			block.setType(type);
			block.setBlockData(Main.m.getServer().createBlockData(data));
		}
	}
}
