package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.RainbowUtility;
import com.magicpowered.rainbowutility.Storage.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AtHim implements Listener {
    private final RainbowUtility plugin;
    private final FileManager fileManager;
    private Message message;

    public AtHim(RainbowUtility plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        loadMessageConfig();
    }

    private void loadMessageConfig() {
        FileConfiguration cfg = fileManager.getConfig();
        message = new Message(
                cfg.getString("AtHim.Message.chat"),
                cfg.getString("AtHim.Message.actionBar"),
                cfg.getString("AtHim.Message.Title.title"),
                cfg.getString("AtHim.Message.Title.subTitle"),
                cfg.getInt("AtHim.Message.Title.Settings.fadeIn"),
                cfg.getInt("AtHim.Message.Title.Settings.stay"),
                cfg.getInt("AtHim.Message.Title.Settings.fadeOut")
        );
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        if (message.contains("@")) {

            if (!event.getPlayer().hasPermission("rainbowutility.use.athim")) {
                sender.sendMessage("§7[§6彩虹工具§7] §c您没有使用此功能的权限");
                return;
            }

            String[] words = message.split(" ");
            for (String word : words) {
                if (word.startsWith("@")) {
                    String targetName = word.substring(1); // remove '@'
                    Player target = Bukkit.getPlayer(targetName);
                    if (target != null && target.isOnline()) {
                        if (!fileManager.getDatabase().getBoolean("AtHim." + target.getUniqueId(), true)) {
                            sender.sendMessage(fileManager.getMessage("AtHim", "targetPlayerDeny").replace("%athim_target%", sender.getName()));
                            continue;
                        }

                        sendMessage(sender, target);
                    }
                }
            }
        }
    }

    private void sendMessage(Player sender, Player target) {
        String useMode = fileManager.getDatabase().getString("AtHim." + target.getUniqueId() + ".message", "chat");
        switch (useMode) {
            case "chat":
                target.sendMessage(message.chat.replace("&", "§")
                                               .replace("%athim_sender%", sender.getName()));
                break;
            case "actionbar":
                target.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message.actionBar.replace("&", "§")
                                                                                                                      .replace("%athim_sender%", sender.getName())));
                break;
            case "title":
                target.sendTitle(
                        message.title.replace("&", "§")
                                     .replace("%athim_sender%", sender.getName()),
                        message.subTitle.replace("&", "§")
                                        .replace("%athim_sender%", sender.getName()),
                        message.fadeIn,
                        message.stay,
                        message.fadeOut
                );
        }
    }
}