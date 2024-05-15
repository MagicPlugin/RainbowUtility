package com.magicpowered.rainbowutility;

import com.magicpowered.rainbowutility.Utility.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class RainbowUtility extends JavaPlugin implements Listener {

    private FileManager fileManager;
    private AntiEnderMan antiEnderMan;
    private AtHim atHim;
    private BackTo backTo;
    private CleanScreen cleanScreen;
    private CustomVision customVision;
    private DropStop dropStop;
    private GetHelp getHelp;
    private ProtectFarmland protectFarmland;
    private CommandListener commandListener;
    private AntiSandFall antiSandFall;

    private boolean antiEnderManEnable;
    private boolean antiSandFallEnable;
    private boolean atHimEnable;
    private boolean backToEnable;
    private boolean cleanScreenEnable;
    private boolean customVisionEnable;
    private boolean dropStopEnable;
    private boolean getHelpEnable;
    private boolean protectFarmlandEnable;
    private boolean feedBackEnable;

    @Override
    public void onEnable() {
        try {
            fileManager = new FileManager(this);
            loadModuleState();

            if (fileManager.getConfig().getBoolean("AntiEnderMan.enable", false)) {
                antiEnderMan = new AntiEnderMan(fileManager);
            }
            if (fileManager.getConfig().getBoolean("AntiSandFall.enable", false)) {
                antiSandFall = new AntiSandFall(fileManager);
            }
            if (fileManager.getConfig().getBoolean("AtHim.enable", false)) {
                atHim = new AtHim(this, fileManager);
            }
            if (fileManager.getConfig().getBoolean("BackTo.enable", false)) {
                backTo = new BackTo(this, fileManager);
            }
            if (fileManager.getConfig().getBoolean("CleanScreen.enable", false)) {
                cleanScreen = new CleanScreen(fileManager);
            }
            if (fileManager.getConfig().getBoolean("CustomVision.enable", false)) {
                customVision = new CustomVision(fileManager);
            }
            if (fileManager.getConfig().getBoolean("DropStop.enable", false)) {
                dropStop = new DropStop(fileManager);
            }
            if (fileManager.getConfig().getBoolean("GetHelp.enable", false)) {
                getHelp = new GetHelp(this, fileManager);
            }
            if (fileManager.getConfig().getBoolean("ProtectFarmland.enable", false)) {
                protectFarmland = new ProtectFarmland(fileManager);
            }

            commandListener = new CommandListener(this, fileManager);

            // 注册命令
            getCommand("ru").setExecutor(commandListener);
            getCommand("back").setExecutor(commandListener);
            getCommand("ru").setTabCompleter(commandListener);
            getCommand("back").setTabCompleter(commandListener);

            getServer().getPluginManager().registerEvents(this, this);
            getServer().getPluginManager().registerEvents(atHim,this);
            getServer().getPluginManager().registerEvents(backTo,this);
            getServer().getPluginManager().registerEvents(antiEnderMan, this);
            getServer().getPluginManager().registerEvents(customVision, this);
            getServer().getPluginManager().registerEvents(dropStop, this);
            getServer().getPluginManager().registerEvents(protectFarmland, this);
            getServer().getPluginManager().registerEvents(antiSandFall, this);

            String server = getServer().getName().toLowerCase();

            Bukkit.getServer().getLogger().info(" ");
            Bukkit.getServer().getLogger().info("  '||    ||' '||''|.         '||''|.   '||'  '|'    妙控动力 MagicPowered");
            Bukkit.getServer().getLogger().info("   |||  |||   ||   ||   ||   '||   ||   ||    |     彩虹系列 RainbowSeries");
            Bukkit.getServer().getLogger().info("   |'|..'||   ||...|'         ||''|'    ||    |     彩虹工具 RainbowUtility v24.0.2.1");
            Bukkit.getServer().getLogger().info("   | '|' ||   ||        ||    ||   |.   ||    |     由 JLING 制作");
            Bukkit.getServer().getLogger().info("  .|. | .||. .||.            .||.  '|'   '|..'      https://magicpowered.cn");
            Bukkit.getServer().getLogger().info(" ");
            Bukkit.getServer().getLogger().info(ChatColor.YELLOW + "  七彩虹桥横九州，工匠巧手织梦游");
            Bukkit.getServer().getLogger().info(" ");
            if (server.contains("arclight")) {
                Bukkit.getServer().getLogger().warning("  当前插件在 Arclight 服务端运行，请注意，这是一个混合服务端，一些模块可能不可用");
            } else if (server.contains("catserver")) {
                Bukkit.getServer().getLogger().warning("  当前插件在 CatServer 服务端运行，请注意，这是一个混合服务端，一些模块可能不可用");
            } else if (server.contains("mohist")) {
                Bukkit.getServer().getLogger().warning("  当前插件在 Mohist 服务端运行，请注意，这是一个混合服务端，一些模块可能不可用");
            }
        } catch (Exception e) {
            Bukkit.getServer().getLogger().info("[彩虹工具] 启动失败!");
            e.printStackTrace();
        }
    }

    public void loadModuleState() {
        FileConfiguration cfg = getConfig();
        this.antiEnderManEnable = cfg.getBoolean("AntiEnderMan.enable");
        this.antiSandFallEnable = cfg.getBoolean("AntiSandFall.enable");
        this.atHimEnable = cfg.getBoolean("AtHim.enable");
        this.backToEnable = cfg.getBoolean("BackTo.enable");
        this.cleanScreenEnable = cfg.getBoolean("CleanScreen.enable");
        this.customVisionEnable = cfg.getBoolean("CustomVision.enable");
        this.dropStopEnable = cfg.getBoolean("DropStop.enable");
        this.getHelpEnable = cfg.getBoolean("GetHelp.enable");
        this.protectFarmlandEnable = cfg.getBoolean("ProtectFarmland.enable");
        this.feedBackEnable = cfg.getBoolean("FeedBack.enable");
    }

    public void reloadPlugin() {
        fileManager.reloadConfig();
        antiEnderMan.reloadAntiEnderMan();
        antiSandFall.reloadAntiSandFall();
        backTo.reloadBackTo();
        protectFarmland.reloadProtectFarmland();
    }

    public boolean isAntiEnderManEnabled() {
        return antiEnderManEnable;
    }

    public boolean isAntiSandFallEnabled() {
        return antiSandFallEnable;
    }

    public boolean isAtHimEnabled() {
        return atHimEnable;
    }

    public boolean isBackToEnabled() {
        return backToEnable;
    }

    public boolean isCleanScreenEnabled() {
        return cleanScreenEnable;
    }

    public boolean isCustomVisionEnabled() {
        return customVisionEnable;
    }

    public boolean isDropStopEnabled() {
        return dropStopEnable;
    }

    public boolean isGetHelpEnabled() {
        return getHelpEnable;
    }

    public boolean isProtectFarmlandEnabled() {
        return protectFarmlandEnable;
    }

    public boolean isFeedBackEnable() {
        return feedBackEnable;
    }


    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equalsIgnoreCase("RainbowUtility")) {
            Bukkit.getServer().getLogger().info("[彩虹工具] 彩虹光照，世界依然，再会!");
        }
    }

    @Override
    public void onDisable() {
        fileManager.saveDatabase();;
        fileManager.saveConfig();
    }

}
