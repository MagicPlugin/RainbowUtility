package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.RainbowUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.entity.Player;

import java.util.List;

public class ExperienceHandler implements Listener {
    private final FileManager fileManager;
    private final FileConfiguration config;

    public ExperienceHandler(FileManager fileManager) {
        this.fileManager = fileManager;
        this.config = fileManager.getConfig();
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        int originalExp = event.getAmount();

        List<String> permissions = config.getStringList("Experience.Permissions");
        double multiplier = 1.0;

        for (String perm : permissions) {
            if (player.hasPermission(perm)) {
                String[] parts = perm.split("\\.");
                String multiplierString = parts[parts.length - 1];
                try {
                    multiplier = Double.parseDouble(multiplierString) / 100.0;
                    break;
                } catch (NumberFormatException e) {
                    Bukkit.getLogger().warning("Invalid permission format: " + perm);
                }
            }
        }

        int modifiedExp = (int) (originalExp * multiplier);
        event.setAmount(modifiedExp);

        String message = config.getString("Experience.message");
        if (message != null) {
            message = message.replace("%multiplier%", String.valueOf(multiplier * 100) + "%");
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));

        }
    }
}