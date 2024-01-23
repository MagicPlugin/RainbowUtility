package com.magicpowered.rainbowutility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DropStop implements Listener {

    private final FileManager fileManager;

    public DropStop(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public static boolean isDayAllowanceExpired(UUID uuid) {
        if(dayAllowances.containsKey(uuid)) {
            if(System.currentTimeMillis() > dayAllowances.get(uuid)) {
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

        // Load permanent allowances
        if (fileManager.getDatabase().contains("DropStop." + playerUUID + ".permanentAllowances") &&
                fileManager.getDatabase().getBoolean("DropStop." + playerUUID + ".permanentAllowances")) {
            permanentAllowances.add(playerUUID);
        }

        // Check if day allowance is expired
        if(isDayAllowanceExpired(playerUUID)) {
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

        // Save permanent allowances
        if (permanentAllowances.contains(playerUUID)) {
            fileManager.getDatabase().set("DropStop." + playerUUID + ".permanentAllowances", true);
        }

        fileManager.saveDatabase();
    }



    private static Map<UUID, Integer> allowances = new HashMap<>();
    private static Map<UUID, Long> dayAllowances = new HashMap<>();
    private static Set<UUID> permanentAllowances = new HashSet<>();

    public static void setAllowance(UUID uuid, int count) {
        allowances.put(uuid, count);
    }

    public static void setDayAllowance(UUID uuid, int days) {
        dayAllowances.put(uuid, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days));
    }

    public static void setPermanentAllowance(UUID uuid) {
        permanentAllowances.add(uuid);
    }

    public static void enableDropProtection(UUID uuid) {
        allowances.remove(uuid);
        dayAllowances.remove(uuid);
        permanentAllowances.remove(uuid);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        if (permanentAllowances.contains(uuid)) return;

        if (dayAllowances.containsKey(uuid)) {
            if (System.currentTimeMillis() > dayAllowances.get(uuid)) {
                dayAllowances.remove(uuid);
            } else {
                return;
            }
        }

        if (allowances.containsKey(uuid)) {
            int count = allowances.get(uuid);

            switch(count) {
                case 3:
                    event.getPlayer().sendMessage(fileManager.getMessage("DropStop","remainingDrops")
                            .replace("%rainbowutility_dropstop_remain%", Integer.toString(3)));
                    break;
                case 2:
                    event.getPlayer().sendMessage(fileManager.getMessage("DropStop","remainingDrops")
                            .replace("%rainbowutility_dropstop_remain%", Integer.toString(2)));
                    break;
                case 1:
                    event.getPlayer().sendMessage(fileManager.getMessage("DropStop","remainingDrops")
                            .replace("%rainbowutility_dropstop_remain%", Integer.toString(1)));
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
        event.getPlayer().sendTitle(fileManager.getConfig().getString("DropStop.Title.title", "&6丢弃保护").replace("&", "§"),
                                    fileManager.getConfig().getString("DropStop.Title.subtitle", "&a使用 /ru ds 设置").replace("&", "§"),
                                    fileManager.getConfig().getInt("DropStop.Title.fadeIn", 10),
                                    fileManager.getConfig().getInt("DropStop.Title.stay", 30),
                                    fileManager.getConfig().getInt("DropStop.Title.fadeOut", 10));
    }


}
