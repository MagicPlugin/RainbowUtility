package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.RainbowUtility;
import com.magicpowered.rainbowutility.Storage.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class BackTo implements Listener {

    private RainbowUtility plugin;
    private FileManager fileManager;
    private HashMap<UUID, Integer> shiftCount = new HashMap<>();
    private static HashMap<UUID, Location> savedLocations = new HashMap<>();
    private Message savingMessage;
    private Message savedMessage;

    public BackTo(RainbowUtility plugin ,FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        loadSavingMessageConfig();
        loadSavedMessageConfig();
    }

    public void reloadBackTo() {
        loadSavedMessageConfig();
        loadSavingMessageConfig();
    }

    private void loadSavingMessageConfig() {
        FileConfiguration cfg = fileManager.getConfig();
        savingMessage = new Message(
                cfg.getString("BackTo.Saving.chat"),
                cfg.getString("BackTo.Saving.actionBar"),
                cfg.getString("BackTo.Saving.Title.title"),
                cfg.getString("BackTo.Saving.Title.subTitle"),
                cfg.getInt("BackTo.Saving.Title.Settings.fadeIn"),
                cfg.getInt("BackTo.Saving.Title.Settings.stay"),
                cfg.getInt("BackTo.Saving.Title.Settings.fadeOut")
        );
    }

    private void loadSavedMessageConfig() {
        FileConfiguration cfg = fileManager.getConfig();
        savedMessage = new Message(
                cfg.getString("BackTo.Saved.chat"),
                cfg.getString("BackTo.Saved.actionBar"),
                cfg.getString("BackTo.Saved.Title.title"),
                cfg.getString("BackTo.Saved.Title.subTitle"),
                cfg.getInt("BackTo.Saved.Title.Settings.fadeIn"),
                cfg.getInt("BackTo.Saved.Title.Settings.stay"),
                cfg.getInt("BackTo.Saved.Title.Settings.fadeOut")
        );
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!player.hasPermission("rainbowutility.use.back")) {
            return;
        }

        int defaultDelay = 60;
        int delayTime = fileManager.getDatabase().getInt("BackTo." + uuid + ".time", defaultDelay);

        if (player.isSneaking() && player.isOnGround()) {
            int count = shiftCount.getOrDefault(uuid, 0) + 1;

            if (count == 2) {
                sendSavingMessage(player);
            } else if (count == 3) {
                savedLocations.put(uuid, player.getLocation());
                sendSavedMessage(player);
                count = 0;
            }

            shiftCount.put(uuid, count);
            Bukkit.getScheduler().runTaskLater(plugin, () -> shiftCount.put(uuid, 0), delayTime);
        }
    }



    public static Location getSavedLocation(Player player) {
        return savedLocations.get(player.getUniqueId());
    }

    private void sendSavingMessage(Player player) {
        String useMode = fileManager.getDatabase().getString("BackTo." + player.getUniqueId() + ".message", "chat");
        switch (useMode) {
            case "chat":
                player.sendMessage(savingMessage.chat.replace("&", "§"));
                break;
            case "actionbar":
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(savingMessage.actionBar.replace("&", "§")));
                break;
            case "title":
                player.sendTitle(
                        savingMessage.title.replace("&", "§"),
                        savingMessage.subTitle.replace("&", "§"),
                        savingMessage.fadeIn,
                        savingMessage.stay,
                        savingMessage.fadeOut
                );
        }
    }

    private void sendSavedMessage(Player player) {
        String useMode = fileManager.getDatabase().getString("BackTo." + player.getUniqueId() + ".message", "chat");
        switch (useMode) {
            case "chat":
                player.sendMessage(savedMessage.chat.replace("&", "§"));
                break;
            case "actionbar":
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(savedMessage.actionBar.replace("&", "§")));
                break;
            case "title":
                player.sendTitle(
                        savedMessage.title.replace("&", "§"),
                        savedMessage.subTitle.replace("&", "§"),
                        savedMessage.fadeIn,
                        savedMessage.stay,
                        savedMessage.fadeOut
                );
        }
    }

}
