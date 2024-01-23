package com.magicpowered.rainbowutility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;


public class RainbowUtility extends JavaPlugin implements Listener {

    // Instance variable for singleton design pattern

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


    @Override
    public void onEnable() {
        try {
            fileManager = new FileManager(this);
            antiEnderMan = new AntiEnderMan(fileManager);
            atHim = new AtHim(this, fileManager);
            backTo = new BackTo(this, fileManager);
            cleanScreen = new CleanScreen(fileManager);
            customVision = new CustomVision(fileManager);
            dropStop = new DropStop(fileManager);
            getHelp = new GetHelp(this, fileManager);
            protectFarmland = new ProtectFarmland(this, fileManager);
            antiSandFall = new AntiSandFall(fileManager);
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
//            getServer().getPluginManager().registerEvents(showItemChat, this);

            String server = getServer().getName().toLowerCase();

            Bukkit.getServer().getLogger().info(" ");
            Bukkit.getServer().getLogger().info("  '||    ||' '||''|.         '||''|.   '||'  '|'    妙控动力 MagicPowered");
            Bukkit.getServer().getLogger().info("   |||  |||   ||   ||   ||   '||   ||   ||    |     彩虹系列 RainbowSeries");
            Bukkit.getServer().getLogger().info("   |'|..'||   ||...|'         ||''|'    ||    |     彩虹工具 RainbowUtility v24.0.1.0");
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
        } catch (Exception e) {
            Bukkit.getServer().getLogger().info("[彩虹工具] 启动失败!");
            e.printStackTrace();
        }
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
