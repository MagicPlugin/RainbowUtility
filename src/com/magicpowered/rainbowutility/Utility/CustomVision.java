package com.magicpowered.rainbowutility.Utility;

import com.comphenix.protocol.ProtocolManager;
import com.magicpowered.rainbowutility.FileManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.util.UUID;

import static org.bukkit.WeatherType.CLEAR;
import static org.bukkit.WeatherType.DOWNFALL;

public class CustomVision implements Listener {

    private FileManager fileManager;

    public CustomVision (FileManager fileManager) {
        this.fileManager = fileManager;
    }


    public void setWeather(Player player, String weather) {
        if(weather.equals("clear")) {
            player.setPlayerWeather(CLEAR);
            player.sendMessage(fileManager.getMessage("CustomVision.Weather", "clear").replace("&", "§"));

        } else if(weather.equals("downfall")) {
            player.setPlayerWeather(DOWNFALL);
            player.sendMessage(fileManager.getMessage("CustomVision.Weather", "downfall").replace("&", "§"));
        }
    }

    public void setTimeLock(Player player, long time) {
        player.setPlayerTime(time, false);
        player.sendMessage(fileManager.getMessage("CustomVision.Time", "lock").replace("%time_lock%", Long.toString(time)));
    }

    public void setTimeUnlock(Player player, long time) {
        player.setPlayerTime(time, true);
        player.sendMessage(fileManager.getMessage("CustomVision.Time", "unlock").replace("%time_unlock%", Long.toString(time)));
    }

}
