package com.byebye.minigames;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Events implements Listener {

	@EventHandler
	public void onPlayerClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (player.getInventory().getItemInMainHand() == null
				|| player.getInventory().getItemInMainHand().getType() != Material.GOLDEN_AXE) {
			return;
		}

		if (e.getHand() != EquipmentSlot.HAND) {
			return;
		}

		if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
			e.setCancelled(true);
			Location loc = e.getClickedBlock().getLocation();
			Main.selection1.put(player.getUniqueId(), loc);
			player.sendMessage("§dPrimeiro ponto selecionado: (" + loc.toVector().toString() + ")");
		}
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			e.setCancelled(true);
			Location loc = e.getClickedBlock().getLocation();
			Main.selection2.put(player.getUniqueId(), loc);
			player.sendMessage("§dSegundo ponto selecionado: (" + loc.toVector().toString() + ")");
		}
	}

}
