package com.magicpowered.rainbowutility;

import com.magicpowered.rainbowutility.Utility.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
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
    private ColorName colorName;
    private ExperienceHandler experienceHandler;

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
    private boolean colorNameEnable;
    private boolean experienceHandlerEnable;

    @Override
    public void onEnable() {
        try {
            fileManager = new FileManager(this);

            Bukkit.getServer().getLogger().info("[彩虹工具] 开始加载模块");

            loadModuleState();

            if (antiEnderManEnable) {
                antiEnderMan = new AntiEnderMan(fileManager);
                Bukkit.getLogger().info("[彩虹工具] AntiEnderMan 被加载");
            }
            if (antiSandFallEnable) {
                antiSandFall = new AntiSandFall(fileManager);
                Bukkit.getLogger().info("[彩虹工具] AntiSandFall 被加载");
            }
            if (atHimEnable) {
                atHim = new AtHim(this, fileManager);
                Bukkit.getLogger().info("[彩虹工具] AtHim 被加载");
            }
            if (backToEnable) {
                backTo = new BackTo(this, fileManager);
                Bukkit.getLogger().info("[彩虹工具] BackTo 被加载");
            }
            if (cleanScreenEnable) {
                cleanScreen = new CleanScreen(fileManager);
                Bukkit.getLogger().info("[彩虹工具] CleanScreen 被加载");
            }
            if (customVisionEnable) {
                customVision = new CustomVision(fileManager);
                Bukkit.getLogger().info("[彩虹工具] CustomVision 被加载");
            }
            if (dropStopEnable) {
                dropStop = new DropStop(fileManager);
                Bukkit.getLogger().info("[彩虹工具] DropStop 被加载");
            }
            if (getHelpEnable) {
                getHelp = new GetHelp(this, fileManager);
                Bukkit.getLogger().info("[彩虹工具] GetHelp 被加载");
            }
            if (protectFarmlandEnable) {
                protectFarmland = new ProtectFarmland(fileManager);
                Bukkit.getLogger().info("[彩虹工具] ProtectFarmland 被加载");
            }
            if (colorNameEnable) {
                colorName = new ColorName(this, fileManager);
                Bukkit.getLogger().info("[彩虹工具] ColorName 被加载");
            }
            if (experienceHandlerEnable) {
                experienceHandler = new ExperienceHandler(fileManager);
                Bukkit.getLogger().info("[彩虹工具] Experience 被加载");
            }
            if (feedBackEnable) {
                Bukkit.getLogger().info("[彩虹工具] FeedBack 被加载");
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
            Bukkit.getServer().getLogger().info("   |'|..'||   ||...|'         ||''|'    ||    |     彩虹工具 RainbowUtility v24.0.2.11");
            Bukkit.getServer().getLogger().info("   | '|' ||   ||        ||    ||   |.   ||    |     由 JLING 制作");
            Bukkit.getServer().getLogger().info("  .|. | .||. .||.            .||.  '|'   '|..'      https://magicpowered.cn");
            Bukkit.getServer().getLogger().info(" ");
            if (server.contains("arclight")) {
                Bukkit.getServer().getLogger().warning("  当前插件在 Arclight 服务端运行，请注意，这是一个混合服务端，一些模块可能不可用");
            } else if (server.contains("catserver")) {
                Bukkit.getServer().getLogger().warning("  当前插件在 CatServer 服务端运行，请注意，这是一个混合服务端，一些模块可能不可用");
            } else if (server.contains("mohist")) {
                Bukkit.getServer().getLogger().warning("  当前插件在 Mohist 服务端运行，请注意，这是一个混合服务端，一些模块可能不可用");
            }
            Bukkit.getServer().getLogger().info(" ");
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
        this.colorNameEnable = cfg.getBoolean("ColorName.enable");
        this.experienceHandlerEnable = cfg.getBoolean("Experience.enable");
    }

    public void reloadPlugin() {
        fileManager.reloadConfig();
        if (antiEnderManEnable) antiEnderMan.reloadAntiEnderMan();
        if (antiSandFallEnable) antiSandFall.reloadAntiSandFall();
        if (backToEnable) backTo.reloadBackTo();
        if (protectFarmlandEnable) protectFarmland.reloadProtectFarmland();
        if (dropStopEnable) dropStop.reloadDropStop();
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

    public boolean isColorNameEnable() {
        return colorNameEnable;
    }

    public DropStop getDropStop() {
        return dropStop;
    }

    public CustomVision getCustomVision() {
        return customVision;
    }

    public GetHelp getGetHelp() {
        return getHelp;
    }

    public ColorName getColorName() {
        return colorName;
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
