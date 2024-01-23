package com.magicpowered.rainbowutility;

import com.comphenix.protocol.ProtocolManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.util.UUID;

import static org.bukkit.WeatherType.CLEAR;
import static org.bukkit.WeatherType.DOWNFALL;

public class CustomVision implements Listener {

    private FileManager fileManager;
    private ProtocolManager protocolManager;

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

    public void setTime(Player player, long time) {
        player.setPlayerTime(time, isTimeLockedFor(player));
        player.sendMessage(fileManager.getMessage("CustomVision.Time", "set").replace("%rainbowutility_customvision_time%", Long.toString(time)));
    }

    public void setTimeLock(Player player, long time) {
        player.setPlayerTime(time, isTimeLockedFor(player));
    }

    private boolean isTimeLockedFor(Player player) {
        UUID uuid = player.getUniqueId();
        return fileManager.getDatabase().getBoolean("CustomVision." + uuid + ".time.lock", true);
    }


}
