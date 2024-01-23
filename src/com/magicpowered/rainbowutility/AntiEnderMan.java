package com.magicpowered.rainbowutility;

import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class AntiEnderMan implements Listener {

    private final FileManager fileManager;

    public AntiEnderMan(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onEndermanTarget(EntityTargetLivingEntityEvent event) {
        World world = event.getEntity().getWorld();
        if (fileManager.getConfig().getBoolean("AntiEnderMan." + world.getName(), false)) {

            if (event.getEntity().getType() == EntityType.ENDERMAN &&
                    event.getTarget() != null &&
                    event.getTarget().getType() == EntityType.ENDERMITE) {
                event.setCancelled(true);
            }
        }
    }
}
