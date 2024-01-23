package com.magicpowered.rainbowutility;

import com.sun.tools.javac.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    public BackTo(RainbowUtility plugin ,FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }


    private HashMap<UUID, Integer> shiftCount = new HashMap<>();
    private static HashMap<UUID, Location> savedLocations = new HashMap<>();


    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!player.hasPermission("rainbowutility.use.back")) {
            return;
        }

        int defaultDelay = 60;
        int delayTime = fileManager.getDatabase().getInt("BackTo." + uuid + ".time", defaultDelay);

        String notifyType = fileManager.getDatabase().getString("BackTo." + uuid + ".info", "title");

        if (player.isSneaking() && player.isOnGround()) {
            int count = shiftCount.getOrDefault(uuid, 0) + 1;

            if (count == 2) {
                switch (notifyType) {
                    case "title":
                        player.sendTitle(
                                fileManager.getConfig().getString("BackTo.saving.Title.title", "§6彩虹工具").replace("&", "§"),
                                fileManager.getConfig().getString("saving.Title.subtitle", "§a再次潜行以保存传送点").replace("&", "§"),
                                fileManager.getConfig().getInt("saving.Title.fadeIn", 20),
                                fileManager.getConfig().getInt("saving.Title.stay", 30),
                                fileManager.getConfig().getInt("saving.Title.fadeOut", 20)
                        );
                        break;
                    case "actionbar":
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(fileManager.getConfig().getString("saving.Actionbar", "§6再次潜行以保存传送点").replace("&", "§")));
                        break;
                    case "chat":
                        player.sendMessage(fileManager.getConfig().getString("saving.Chat", "§7[§6彩虹工具§7] 再次潜行以保存传送点").replace("&", "§"));
                        break;
                }
            } else if (count == 3) {
                savedLocations.put(uuid, player.getLocation());

                switch (notifyType) {
                    case "title":
                        player.sendTitle(
                                fileManager.getConfig().getString("saveSuccess.Title.title", "§6彩虹工具").replace("&", "§"),
                                fileManager.getConfig().getString("saveSuccess.Title.subtitle", "§a成功保存传送点").replace("&", "§"),
                                fileManager.getConfig().getInt("saveSuccess.Title.fadeIn", 20),
                                fileManager.getConfig().getInt("saveSuccess.Title.stay", 30),
                                fileManager.getConfig().getInt("saveSuccess.Title.fadeOut", 20)
                        );
                        break;
                    case "actionbar":
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(fileManager.getConfig().getString("saveSuccess.Actionbar", "§6成功保存传送点").replace("&", "§")));
                        break;
                    case "chat":
                        player.sendMessage(fileManager.getConfig().getString("saveSuccess.Chat", "§7[§6彩虹工具§7] 成功保存传送点").replace("&", "§"));
                        break;
                }

                count = 0; // reset counter
            }

            shiftCount.put(uuid, count);
            Bukkit.getScheduler().runTaskLater(plugin, () -> shiftCount.put(uuid, 0), delayTime);
        }
    }


    public static Location getSavedLocation(Player player) {
        return savedLocations.get(player.getUniqueId());
    }

}
