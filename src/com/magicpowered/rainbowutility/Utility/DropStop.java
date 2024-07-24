package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.Storage.Message;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DropStop implements Listener {

    private final FileManager fileManager;
    private Message message;

    public DropStop(FileManager fileManager) {
        this.fileManager = fileManager;
        loadMessageConfig();
    }

    public void reloadDropStop() {
        loadMessageConfig();
    }

    private void loadMessageConfig() {
        FileConfiguration cfg = fileManager.getConfig();
        message = new Message(
                cfg.getString("DropStop.Message.chat"),
                cfg.getString("DropStop.Message.actionBar"),
                cfg.getString("DropStop.Message.Title.title"),
                cfg.getString("DropStop.Message.Title.subTitle"),
                cfg.getInt("DropStop.Message.Title.Settings.fadeIn"),
                cfg.getInt("DropStop.Message.Title.Settings.stay"),
                cfg.getInt("DropStop.Message.Title.Settings.fadeOut")
        );
    }

    public static boolean isDayAllowanceExpired(UUID uuid) {
        if (dayAllowances.containsKey(uuid)) {
            if (System.currentTimeMillis() > dayAllowances.get(uuid)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Load allowances
        if (fileManager.getDatabase().contains("DropStop." + playerUUID + ".allowances")) {
            allowances.put(playerUUID, fileManager.getDatabase().getInt("DropStop." + playerUUID + ".allowances"));
        }

        // Load day allowances
        if (fileManager.getDatabase().contains("DropStop." + playerUUID + ".dayAllowances")) {
            dayAllowances.put(playerUUID, fileManager.getDatabase().getLong("DropStop." + playerUUID + ".dayAllowances"));
        }

        // Load min allowances
        if (fileManager.getDatabase().contains("DropStop." + playerUUID + ".minAllowances")) {
            minAllowances.put(playerUUID, fileManager.getDatabase().getLong("DropStop." + playerUUID + ".minAllowances"));
        }

        // Load permanent allowances
        if (fileManager.getDatabase().contains("DropStop." + playerUUID + ".permanentAllowances") &&
                fileManager.getDatabase().getBoolean("DropStop." + playerUUID + ".permanentAllowances")) {
            permanentAllowances.add(playerUUID);
        }

        // Check if day allowance is expired
        if (isDayAllowanceExpired(playerUUID)) {
            event.getPlayer().sendMessage(fileManager.getMessage("DropStop", "protectionExpired"));
            dayAllowances.remove(playerUUID); // 清除该玩家的日期记录，避免重复提醒
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Save allowances
        if (allowances.containsKey(playerUUID)) {
            fileManager.getDatabase().set("DropStop." + playerUUID + ".allowances", allowances.get(playerUUID));
        }

        // Save day allowances
        if (dayAllowances.containsKey(playerUUID)) {
            fileManager.getDatabase().set("DropStop." + playerUUID + ".dayAllowances", dayAllowances.get(playerUUID));
        }

        // Save min allowances
        if (minAllowances.containsKey(playerUUID)) {
            fileManager.getDatabase().set("DropStop." + playerUUID + ".minAllowances", minAllowances.get(playerUUID));
        }

        // Save permanent allowances
        if (permanentAllowances.contains(playerUUID)) {
            fileManager.getDatabase().set("DropStop." + playerUUID + ".permanentAllowances", true);
        }

        fileManager.saveDatabase();
    }

    private static Map<UUID, Integer> allowances = new HashMap<>();
    private static Map<UUID, Long> dayAllowances = new HashMap<>();
    private static Map<UUID, Long> minAllowances = new HashMap<>();
    private static Set<UUID> permanentAllowances = new HashSet<>();

    public static void setAllowance(UUID uuid, int count) {
        allowances.put(uuid, count);
    }

    public void setDayAllowance(UUID uuid, int days) {
        dayAllowances.put(uuid, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days));
    }

    public void setMinAllowance(UUID uuid, int mins) {
        minAllowances.put(uuid, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(mins));
    }

    public void setPermanentAllowance(UUID uuid) {
        permanentAllowances.add(uuid);
    }

    public static void enableDropProtection(UUID uuid) {
        allowances.remove(uuid);
        dayAllowances.remove(uuid);
        minAllowances.remove(uuid);
        permanentAllowances.remove(uuid);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // 检查玩家的背包是否有空位
        Player player = event.getPlayer();
        boolean hasEmptySlot = false;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                hasEmptySlot = true;
                break;
            }
        }

        if (!hasEmptySlot) {
            String message = fileManager.getMessage("DropStop", "inventoryFull");
            player.sendMessage(message);
            return;
        }

        if (permanentAllowances.contains(uuid)) return;

        if (dayAllowances.containsKey(uuid)) {
            if (System.currentTimeMillis() > dayAllowances.get(uuid)) {
                dayAllowances.remove(uuid);
            } else {
                return;
            }
        }

        if (minAllowances.containsKey(uuid)) {
            if (System.currentTimeMillis() > minAllowances.get(uuid)) {
                minAllowances.remove(uuid);
            } else {
                return;
            }
        }

        if (allowances.containsKey(uuid)) {
            int count = allowances.get(uuid);

            switch (count) {
                case 3:
                    event.getPlayer().sendMessage(fileManager.getMessage("DropStop", "remainingDrops")
                            .replace("%dropstop_remain%", Integer.toString(3)));
                    break;
                case 2:
                    event.getPlayer().sendMessage(fileManager.getMessage("DropStop", "remainingDrops")
                            .replace("%dropstop_remain%", Integer.toString(2)));
                    break;
                case 1:
                    event.getPlayer().sendMessage(fileManager.getMessage("DropStop", "remainingDrops")
                            .replace("%dropstop_remain%", Integer.toString(1)));
                    break;
            }

            if (count > 1) {
                allowances.put(uuid, count - 1);
            } else {
                allowances.remove(uuid);
            }
            return;
        }

        event.setCancelled(true);
        sendMessage(event.getPlayer());
    }

    private void sendMessage(Player player) {
        String useMode = fileManager.getDatabase().getString("DropStop." + player.getUniqueId() + ".message", "chat");
        switch (useMode) {
            case "chat":
                player.sendMessage(message.chat.replace("&", "§"));
                break;
            case "actionbar":
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message.actionBar.replace("&", "§")));
                break;
            case "title":
                player.sendTitle(
                        message.title.replace("&", "§"),
                        message.subTitle.replace("&", "§"),
                        message.fadeIn,
                        message.stay,
                        message.fadeOut
                );
        }
    }
}
