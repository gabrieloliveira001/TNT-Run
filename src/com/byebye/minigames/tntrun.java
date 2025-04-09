package com.byebye.minigames;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class tntrun implements Listener {

	public static boolean gamestarting = false;
	public static boolean gamestarted = false;
	private static final int GAME_COUNTDOWN_SECONDS = 5;
	private static double FALL_LIMIT_Y;

	public static final HashSet<UUID> isplaying = new HashSet<>();

	public static void startgame() {

		File file = new File(Main.m.getDataFolder(), "locations.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		World world = Bukkit.getWorld(config.getString("locations.tntrun.world"));
		Random random = new Random();

		FALL_LIMIT_Y = config.getDouble("locations.tntrun.y");

		tntrunreset();

		if (gamestarting)
			return;
		gamestarting = true;

		isplaying.clear();
		for (Player p : world.getPlayers()) {
			int n = random.nextInt(4) + 1;
			String path = "locations.tntrun_spawn_" + n;
			if (config.contains(path)) {
				World spawnWorld = Bukkit.getWorld(config.getString(path + ".world"));
				double x = config.getDouble(path + ".x");
				double y = config.getDouble(path + ".y");
				double z = config.getDouble(path + ".z");
				float yaw = (float) config.getDouble(path + ".yaw");
				float pitch = (float) config.getDouble(path + ".pitch");
				Location loc = new Location(spawnWorld, x, y, z, yaw, pitch);
				p.teleport(loc);
				p.setGameMode(GameMode.SURVIVAL);
				isplaying.add(p.getUniqueId());
			}
		}
		
		for (Player p : world.getPlayers()) {
			if (isplaying.contains(p.getUniqueId()))
				p.playSound(p.getLocation(), Sound.ENTITY_TNT_PRIMED, 1f, 1f);
				p.sendTitle("§c§lTNT RUN 🧨", "§f§lCorra!", 10, 40, 10);
		}
		
		new BukkitRunnable() {
			int countdown = GAME_COUNTDOWN_SECONDS;
			@Override
			public void run() {
				if (countdown == 0) {
					for (Player p : world.getPlayers()) {
						if (isplaying.contains(p.getUniqueId()))
							p.sendMessage("§aMinigame iniciado!");
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							if (isplaying.isEmpty()) {
								cancel();
								return;
							}
							for (UUID uuid : isplaying) {
								Player p = Bukkit.getPlayer(uuid);
								if (p == null || !p.isOnline())
									continue;
								Block block = p.getLocation().clone().subtract(0, 1, 0).getBlock();
								if (block.getType() == Material.SAND) {
									new BukkitRunnable() {
										@Override
										public void run() {
											block.getLocation().getBlock().setType(Material.AIR);
											block.getLocation().clone().subtract(0, 1, 0).getBlock()
													.setType(Material.AIR);
										}
									}.runTaskLater(Main.m, 10L);
								}
							}
						}
					}.runTaskTimer(Main.m, 0L, 5L);

					gamestarted = true;
					startGameChecker(world);
					cancel();
					return;
				}
				if (countdown <= 3) {
					for (Player p : world.getPlayers()) {
						if (isplaying.contains(p.getUniqueId())) {
							p.sendMessage("§eIniciando em " + countdown);
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
						}
					}
				}
				countdown--;
			}
		}.runTaskTimer(Main.m, 0L, 20L);
	}

	private static void startGameChecker(World world) {
		if (!gamestarted) {
			return;
		}
		if (isplaying.size() == 1) {
			UUID winnerUUID = isplaying.iterator().next();
			Player winner = Bukkit.getPlayer(winnerUUID);
			if (winner != null && winner.isOnline()) {
				winner.sendMessage("§aVocê venceu o minigame!");
				winner.sendTitle("§a§lVOCÊ VENCEU!!!", "", 10, 40, 10);
				winner.playSound(winner.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10f, 10f);

				new BukkitRunnable() {
					int count = 0;

					@Override
					public void run() {
						if (count >= 20) {
							cancel();
							return;
						}
						spawnFireworks(winner.getLocation());
						count++;
					}
				}.runTaskTimer(Main.m, 0L, 2L);

			}

			tntrunreset5s();
			gamestarted = false;
			gamestarting = false;
			isplaying.clear();
		}
	}

	private static void spawnFireworks(Location loc) {
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BALL_LARGE).build());
		fm.setPower(1);
		fw.setFireworkMeta(fm);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!gamestarted)
			return;
		Player player = event.getPlayer();
		if (!isplaying.contains(player.getUniqueId()))
			return;
		if (player.getLocation().getY() < FALL_LIMIT_Y) {
			File file = new File(Main.m.getDataFolder(), "locations.yml");
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			World world = Bukkit.getWorld(config.getString("locations.tntrun.world"));
			String basePath = "locations.tntrun";
			if (config.contains(basePath)) {
				String worldName = config.getString(basePath + ".world");
				double x = config.getDouble(basePath + ".x");
				double y = config.getDouble(basePath + ".y");
				double z = config.getDouble(basePath + ".z");
				float yaw = (float) config.getDouble(basePath + ".yaw");
				float pitch = (float) config.getDouble(basePath + ".pitch");
				Location spawnLoc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
				player.teleport(spawnLoc);
				player.setGameMode(GameMode.SPECTATOR);
				player.sendTitle("§c§lVocê perdeu", "", 10, 40, 10);
				isplaying.remove(player.getUniqueId());
				startGameChecker(world);
			}
			return;
		}
		if (event.getFrom().getBlockX() != event.getTo().getBlockX()
				|| event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
			Block block = player.getLocation().clone().subtract(0, 1, 0).getBlock();
			if (block.getType() == Material.SAND) {
				block.getLocation().getBlock().setType(Material.AIR);
				block.getLocation().clone().subtract(0, 1, 0).getBlock().setType(Material.AIR);
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		File file = new File(Main.m.getDataFolder(), "locations.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		World world = Bukkit.getWorld(config.getString("locations.tntrun.world"));
		isplaying.remove(e.getPlayer().getUniqueId());
		startGameChecker(world);
	}

	public static void tntrunreset() {
		FileConfiguration locationsConfig = YamlConfiguration
				.loadConfiguration(new File(Main.m.getDataFolder(), "locations.yml"));
		String basePath = "locations.tntrun";
		if (locationsConfig.contains(basePath)) {
			String worldName = locationsConfig.getString(basePath + ".world");
			double x = locationsConfig.getDouble(basePath + ".x");
			double y = locationsConfig.getDouble(basePath + ".y");
			double z = locationsConfig.getDouble(basePath + ".z");
			float yaw = (float) locationsConfig.getDouble(basePath + ".yaw");
			float pitch = (float) locationsConfig.getDouble(basePath + ".pitch");
			Location loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
			SchematicManager.loadSchematic("tntrun", loc);
		}
	}

	public static void tntrunreset5s() {
		FileConfiguration locationsConfig = YamlConfiguration
				.loadConfiguration(new File(Main.m.getDataFolder(), "locations.yml"));
		String basePath = "locations.tntrun";
		if (locationsConfig.contains(basePath)) {
			String worldName = locationsConfig.getString(basePath + ".world");
			double x = locationsConfig.getDouble(basePath + ".x");
			double y = locationsConfig.getDouble(basePath + ".y");
			double z = locationsConfig.getDouble(basePath + ".z");
			float yaw = (float) locationsConfig.getDouble(basePath + ".yaw");
			float pitch = (float) locationsConfig.getDouble(basePath + ".pitch");
			Location loc = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
			new BukkitRunnable() {
				@Override
				public void run() {
					SchematicManager.loadSchematic("tntrun", loc);
				}
			}.runTaskLater(Main.m, 100L);
		}
	}

	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
	    if (!(event.getEntity() instanceof Player)) return;
	    if (event.getDamager() instanceof Firework) {
	        event.setCancelled(true);
	    }
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (tntrun.isplaying.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (tntrun.isplaying.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (tntrun.isplaying.contains(player.getUniqueId())) {
			event.setCancelled(true);
		}
	}
}
