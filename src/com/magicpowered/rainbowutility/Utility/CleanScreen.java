package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.RainbowUtility;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CleanScreen {

    private static FileManager fileManager;

    public CleanScreen (FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public static void clearScreen(Player player) {
        for (int i = 0; i < 50; i++) { // 发送空白行来“清除”聊天
            player.sendMessage("");
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(fileManager.getMessage("CleanScreen.Message", "successClean")));
        }
    }

    public static void clearScreenWithDelay(Player player, int seconds) {
        Bukkit.getScheduler().runTaskLater(RainbowUtility.getPlugin(RainbowUtility.class), () -> {
            clearScreen(player);
        }, 20L * seconds); // 20 ticks = 1 second
    }
}
