package com.magicpowered.rainbowutility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class AntiSandFall implements Listener {

    private final FileManager fileManager;

    public AntiSandFall(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSandFall(EntityChangeBlockEvent event) {
        String world = event.getBlock().getWorld().getName();
        Material material = event.getBlock().getType();
        if (event.getEntityType() == EntityType.FALLING_BLOCK && event.getTo() == Material.AIR) {
            if (fileManager.getConfig().getBoolean("AntiSandFall." + world, false)) {
                if (material == Material.SAND
                        || material == Material.GRAVEL
                        || material == Material.RED_SAND
                        || material == Material.WHITE_CONCRETE_POWDER
                        || material == Material.ORANGE_CONCRETE_POWDER
                        || material == Material.MAGENTA_CONCRETE_POWDER
                        || material == Material.LIGHT_BLUE_CONCRETE_POWDER
                        || material == Material.YELLOW_CONCRETE_POWDER
                        || material == Material.LIME_CONCRETE_POWDER
                        || material == Material.PINK_CONCRETE_POWDER
                        || material == Material.GRAY_CONCRETE_POWDER
                        || material == Material.LIGHT_GRAY_CONCRETE_POWDER
                        || material == Material.CYAN_CONCRETE_POWDER
                        || material == Material.PURPLE_CONCRETE_POWDER
                        || material == Material.BLUE_CONCRETE_POWDER
                        || material == Material.BROWN_CONCRETE_POWDER
                        || material == Material.GREEN_CONCRETE_POWDER
                        || material == Material.RED_CONCRETE_POWDER
                        || material == Material.BLACK_CONCRETE_POWDER) {
                    event.setCancelled(true);
                    event.getBlock().getState().update(false, false);
                }
            }
        }
    }
}
