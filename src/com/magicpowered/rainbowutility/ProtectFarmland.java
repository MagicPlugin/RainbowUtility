package com.magicpowered.rainbowutility;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectFarmland implements Listener {

    private final RainbowUtility plugin;
    private final FileManager fileManager;

    public ProtectFarmland(RainbowUtility plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onStepFarmland(PlayerInteractEvent event) {
        World world = event.getPlayer().getWorld();
        if (fileManager.getConfig().getBoolean("ProtectFarmland." + world.getName(), true)) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FARMLAND) {
                event.setCancelled(true);
            }
        }
    }
}

