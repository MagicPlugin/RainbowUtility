package com.magicpowered.rainbowutility;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;

public class FileManager {

    private final RainbowUtility plugin;
    private FileConfiguration config;
    private FileConfiguration database;
    private FileConfiguration feedback;

    private File configFile;
    private File databaseFile;
    private File feedbackFile;

    public FileManager(RainbowUtility plugin) {
        this.plugin = plugin;
        saveDefaultConfig();
    }

    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultConfigStream = plugin.getResource("config.yml");
        if (defaultConfigStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
            config.setDefaults(defaultConfig);
        }

        if (databaseFile == null) {
            databaseFile = new File(plugin.getDataFolder(), "database.yml");
        }
        database = YamlConfiguration.loadConfiguration(databaseFile);

        if (feedbackFile == null) {
            feedbackFile = new File(plugin.getDataFolder(), "feedback.yml");
        }
        feedback = YamlConfiguration.loadConfiguration(feedbackFile);

    }

    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public FileConfiguration getDatabase() {
        if (database == null) {
            reloadConfig();
        }
        return database;
    }

    public FileConfiguration getFeedback() {
        if (feedback == null) {
            reloadConfig();
        }
        return feedback;
    }

    public void saveConfig() {
        if (config == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }

    public void saveDatabase() {
        if (database == null || databaseFile == null) {
            return;
        }
        try {
            getDatabase().save(databaseFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save database to " + databaseFile, ex);
        }
    }

    public void saveFeedback() {
        if (feedback == null || feedbackFile == null) {
            return;
        }
        try {
            getFeedback().save(feedbackFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save feedback to " + feedbackFile, ex);
        }
    }



    public void saveDefaultConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        if (databaseFile == null) {
            databaseFile = new File(plugin.getDataFolder(), "database.yml");
        }
        if (!databaseFile.exists()) {
            plugin.saveResource("database.yml", false);
        }
        if (feedbackFile == null) {
            feedbackFile = new File(plugin.getDataFolder(), "feedback.yml");
        }
        if (!feedbackFile.exists()) {
            plugin.saveResource("feedback.yml", false);
        }
    }

    public String getMessage(String mainKey, String subKey) {
        String rawMessage = getConfig().getString(mainKey + "." + subKey, "§7[§6彩虹工具§7] &6警告, 对于 " + mainKey + "." + subKey + " 的消息未设置");
        return rawMessage.replace("&", "§");
    }

    public boolean isFeatureEnabled(String featureName) {

        return config.getBoolean(featureName, true);
    }

    public List<String> getDisabledEndermanWorlds() {
        return config.getStringList("AntiEnderMan.disabledWorlds");
    }

}
