package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.RainbowUtility;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

public class ProtectFarmland implements Listener {
    private final FileManager fileManager;
    private final Set<String> enabledWorlds = new HashSet<>();


    public ProtectFarmland(FileManager fileManager) {
        this.fileManager = fileManager;
        loadWorlds();
    }

    public void reloadProtectFarmland() {
        enabledWorlds.clear();
        loadWorlds();
    }

    private void loadWorlds() {
        for (String key : fileManager.getConfig().getConfigurationSection("AntiSandFall.World").getKeys(false)) {
            if (fileManager.getConfig().getBoolean("AntiSandFall.World." + key)) {
                enabledWorlds.add(key);
            }
        }
    }

    @EventHandler
    public void onStepFarmland(PlayerInteractEvent event) {
        String world = event.getPlayer().getWorld().getName();
        if (enabledWorlds.contains(world)) {
            if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.FARMLAND) {
                event.setCancelled(true);
            }
        }
    }
}

