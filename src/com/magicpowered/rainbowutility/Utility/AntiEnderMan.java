package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import java.util.Set;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashSet;

public class AntiEnderMan implements Listener {
    private final FileManager fileManager;
    private final Set<String> enabledWorlds = new HashSet<>();

    public AntiEnderMan(FileManager fileManager) {
        this.fileManager = fileManager;
        loadWorlds();
    }

    public void reloadAntiEnderMan() {
        enabledWorlds.clear();
        loadWorlds();
    }

    private void loadWorlds() {
        // 假设你的 FileManager 能够以这样的方式返回配置项的键集合
        for (String key : fileManager.getConfig().getConfigurationSection("AntiEnderMan.World").getKeys(false)) {
            if (fileManager.getConfig().getBoolean("AntiEnderMan.World." + key)) {
                enabledWorlds.add(key);
            }
        }
    }

    @EventHandler
    public void onEndermanTarget(EntityTargetLivingEntityEvent event) {
        if (event.getEntity().getType() == EntityType.ENDERMAN &&
                event.getTarget() != null &&
                event.getTarget().getType() == EntityType.ENDERMITE &&
                enabledWorlds.contains(event.getEntity().getWorld().getName())) {
            event.setCancelled(true);
        }
    }
}
