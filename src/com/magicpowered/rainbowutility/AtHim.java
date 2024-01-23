package com.magicpowered.rainbowutility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AtHim implements Listener {
    private final RainbowUtility plugin;
    private final FileManager fileManager;

    public AtHim(RainbowUtility plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        if (message.contains("@")) {
            String[] words = message.split(" ");
            for (String word : words) {
                if (word.startsWith("@")) {
                    String targetName = word.substring(1); // Remove '@'
                    Player target = Bukkit.getPlayer(targetName);
                    if (target != null && target.isOnline()) {
                        if (!fileManager.getDatabase().getBoolean("AtHe." + target.getUniqueId(), true)) {
                            sender.sendMessage(fileManager.getMessage("AtHe", "targetPlayerDeny").replace("%rainbowutility_athe_target%", sender.getName()));
                            continue;
                        }
                        target.sendTitle(fileManager.getConfig().getString("AtHe.Title.title", " ")
                                        .replace("&", "§")
                                        .replace("%rainbowutility_athim_sender%", sender.getName()),
                                fileManager.getConfig().getString("AtHe.Title.subtitle", "&a您被 %rainbowutility_athim_sender% 提到了")
                                        .replace("&", "§")
                                        .replace("%rainbowutility_athim_sender%", sender.getName()),
                                fileManager.getConfig().getInt("AtHe.Title.fadeIn", 10),
                                fileManager.getConfig().getInt("AtHe.Title.stay", 30),
                                fileManager.getConfig().getInt("AtHe.Title.fadeOut", 10));
                    }
                }
            }
        }
    }
}