package com.magicpowered.rainbowutility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang.StringUtils.isNumeric;

public class CommandListener implements CommandExecutor, TabCompleter {

    private final RainbowUtility plugin;

    private static FileConfiguration config;
    private FileManager fileManager;
    private CustomVision customVision;
    private DropStop dropStop;
    private GetHelp getHelp;

    public CommandListener(RainbowUtility plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        this.customVision = new CustomVision(fileManager);
        this.dropStop = new DropStop(fileManager);
        this.getHelp = new GetHelp(plugin, fileManager);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (label.equalsIgnoreCase("back") && player != null) {
            boolean shouldOverride = fileManager.getDatabase().getBoolean("BackTo." + player.getUniqueId() + ".override");
            if (shouldOverride) {
                return handleBackCommand(player, sender);
            }
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage("§7[§c彩虹工具§7] 帮助:");
            sender.sendMessage("§7必要参数: <?>, 可选必要参数: <?/?>, 非必要参数: [?], 可选非必要参数: [?/?]");
            sender.sendMessage("§7  |- §c/ru tools §7- 查看工具包帮助");
            sender.sendMessage("§7  |- §c/ru backto §7- 查看 §c快捷传送点 §7模块帮助");
            sender.sendMessage("§7  |- §c/ru dropstop §7- 查看 §c丢弃保护 §7模块帮助");
            sender.sendMessage("§7  |- §c/ru vision §7- 查看 §c自定义视觉 §7模块帮助");
            sender.sendMessage("§7  |- §c/ru operator §7- 查看 §c管理员任务 §7模块帮助 (管理员)");
            sender.sendMessage("§7  |- §c/ru reload §7- 重新载入配置文件 (管理员)");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "op":
                if (!sender.hasPermission("rainbowutility.use.callop")) {
                    sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数");
                    sender.sendMessage("§7  |用法- §c/ru op 请问如何前往生存世界");
                    return true;
                }

                getHelp.sendOpHelp(player, Arrays.stream(args).skip(1).collect(Collectors.joining(" ")));
                sender.sendMessage(fileManager.getMessage("GetHelp", "successSend"));
                Bukkit.getServer().getLogger().info(Arrays.stream(args).skip(1).collect(Collectors.joining(" ")));
                return true;

            case "at":
                if (args.length != 2) {
                    sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数");
                    sender.sendMessage("§7  |用法- §c/ru at true/false");
                    return true;
                }
                boolean acceptAt = Boolean.parseBoolean(args[1]);
                fileManager.getDatabase().set("AtHe." + player.getUniqueId(), acceptAt);
                String baseMessage = fileManager.getMessage("AtHe", "receiveMode");  // Default message in case the path is missing
                String formattedMessage = acceptAt ? baseMessage.replace("%rainbowutility_athe_mode%", "开启") : baseMessage.replace("%rainbowutility_athe_mode%", "关闭");
                sender.sendMessage(formattedMessage);

                return true;

            case "fb":
                if (!sender.hasPermission("rainbowutility.use.feedback")) {
                    sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数");
                    sender.sendMessage("§7  |用法- §c/ru fb 希望服务器多组织一些活动");
                    return true;
                }

                getHelp.sendFeedback(player, Arrays.stream(args).skip(1).collect(Collectors.joining(" ")));
                break;

            case "cs":
                if (player == null) {
                    Bukkit.getServer().getLogger().info("[彩虹工具] 此命令必须由一个玩家执行");
                    return true;
                }
                if (args.length == 1) {
                    CleanScreen.clearScreen(player);
                } else if (args.length == 2) {
                    try {
                        int delaySeconds = Integer.parseInt(args[1].replace("s", ""));
                        CleanScreen.clearScreenWithDelay(player, delaySeconds);
                        String messageTemplate = fileManager.getMessage("CleanScreen", "beToDo");
                        String message = messageTemplate.replace("{rainbowutility_cleanscreen_time}", String.valueOf(delaySeconds));
                        player.sendMessage(message);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§7[§c彩虹头颅§7] 错误, 参数 " + args[2] + " 不是数字");
                    }
                } else {
                    sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数");
                    sender.sendMessage("§7  |用法- §c/ru cs 100");
                    return true;
                }
                return true;

            case "bk":
                if (args.length >= 2 && args[1].equalsIgnoreCase("set")) {
                    if (player == null) {
                        Bukkit.getServer().getLogger().info("[彩虹工具] 此命令必须由一个玩家执行");
                        return true;
                    }
                    if (args.length != 4) {
                        sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数 <time/command>");
                        sender.sendMessage("§7  |用法- §c/ru bk set time 1");
                        sender.sendMessage("§7  |用法- §c/ru bk set command ru");
                        return true;
                    }
                    if (!sender.hasPermission("rainbowutility.use.backset")) {
                        sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                        return true;
                    }

                    String subCommandBK = args[2].toLowerCase();

                    switch (subCommandBK) {
                        case "time":
                            try {
                                int delayTime = Integer.parseInt(args[3]);
                                if (delayTime <= 0) {
                                    player.sendMessage("§7[§c彩虹工具§7] 错误, [value] 参数必须是一个正整数");
                                    return true;
                                }
                                fileManager.getDatabase().set("BackTo." + player.getUniqueId() + ".time", delayTime);
                                fileManager.saveDatabase();
                                player.sendMessage(fileManager.getMessage("BackTo", "successSetTime").replace("%rainbowutility_backto_time%", Integer.toString(delayTime)));
                            } catch (NumberFormatException e) {
                                sender.sendMessage("§7[§c彩虹头颅§7] 错误, 参数 " + args[3] + " 不是数字");
                            }
                            break;

                        case "command":
                            String choice = args[3].toLowerCase();
                            if (!choice.equals("true") && !choice.equals("false")) {
                                player.sendMessage("§7[§c彩虹工具§7] 错误, 参数 " + choice + "不是布尔值: <true/false>");
                                return true;
                            }
                            boolean isOverride = choice.equals("true");
                            fileManager.getDatabase().set("BackTo." + player.getUniqueId() + ".override", isOverride);
                            fileManager.saveDatabase();
                            String overrideMessage = isOverride ? "开启" : "关闭";
                            player.sendMessage(fileManager.getMessage("BackTo", "successSetCommand").replace("%rainbowutility_backto_command%", overrideMessage));
                            break;

                        case "info":
                            String mode = args[3].toLowerCase();
                            if (!mode.equals("title") && !mode.equals("chat") && !mode.equals("actionbar")) {
                                player.sendMessage("§7[§c彩虹工具§7] 错误, 参数 " + mode + " 不是有效的子命令参数: <title/chat/actionbar>");
                                return true;
                            }
                            fileManager.getDatabase().set("BackTo." + player.getUniqueId() + ".info", mode);
                            fileManager.saveDatabase();
                            player.sendMessage(fileManager.getMessage("BackTo", "infoModeSwitched").replace("%rainbowutility_backto_info%", mode));
                            break;
                        default:
                            sender.sendMessage("§7[§c彩虹头颅§7] 错误，参数 " + args[3] + " 不是有效的状态类型: <time/command/info>");
                            break;
                    }
                } else {
                    return handleBackCommand(player, sender);
                }
                return true;

            case "ds":
                if (player == null) {
                    Bukkit.getServer().getLogger().info("[彩虹工具] 此命令必须由一个玩家执行");
                    return true;
                }
                if(args.length >= 2) {
                    switch(args[1].toLowerCase()) {
                        case "once":
                            if(args.length == 3 && isNumeric(args[2])) {
                                DropStop.setAllowance(player.getUniqueId(), Integer.parseInt(args[2]));
                                String message = fileManager.getMessage("DropStop", "cancelNums")
                                        .replace("%rainbowutility_dropstop_nums%", args[2]);
                                player.sendMessage(message);
                            } else {
                                DropStop.setAllowance(player.getUniqueId(), 1);
                                player.sendMessage(fileManager.getMessage("DropStop", "cancelNext"));
                            }
                            break;
                        case "day":
                            if(args.length == 3 && isNumeric(args[2])) {
                                DropStop.setDayAllowance(player.getUniqueId(), Integer.parseInt(args[2]));
                                String message = fileManager.getMessage("DropStop", "cancelDays")
                                        .replace("%rainbowutility_dropstop_days%", args[2]);
                                player.sendMessage(message);
                            } else {
                                DropStop.setDayAllowance(player.getUniqueId(), 1);
                                player.sendMessage(fileManager.getMessage("DropStop", "cancelDays")
                                        .replace("%rainbowutility_dropstop_days%", "1"));
                            }
                            break;
                        case "keep":
                            DropStop.setPermanentAllowance(player.getUniqueId());
                            player.sendMessage(fileManager.getMessage("DropStop", "cancelKeep"));
                            break;
                        case "true":
                            DropStop.enableDropProtection(player.getUniqueId());
                            player.sendMessage(fileManager.getMessage("DropStop", "enable"));
                            break;
                        default:
                            sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数");
                            sender.sendMessage("§7  |用法- §c/ru ds true");
                            sender.sendMessage("§7  |用法- §c/ru ds keep");
                            sender.sendMessage("§7  |用法- §c/ru ds once 5");
                            sender.sendMessage("§7  |用法- §c/ru ds day 7");
                    }
                } else {
                    sender.sendMessage("§7[§c彩虹工具§7] 错误, 缺少必要参数: <once/day/keep/true>");
                }

                return true;

            case "vs":
                if (player == null) {
                    Bukkit.getServer().getLogger().info("[彩虹工具] 此命令必须由一个玩家执行");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§7[§c彩虹工具§7] 缺少必要参数: <weather/time>");
                    return true;
                }

                String subCommand = args[1].toLowerCase();

                switch (subCommand) {
                    case "weather":
                        if (args.length != 3) {
                            sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数");
                            sender.sendMessage("§7  |用法- §c/ru vs weather clear");
                            return true;
                        }
                        if (!sender.hasPermission("rainbowutility.use.weather")) {
                            sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                            return true;
                        }

                        if (args[2].equalsIgnoreCase("clear")) {
                            customVision.setWeather(player, "clear");
                        } else if (args[2].equalsIgnoreCase("rain")) {
                            customVision.setWeather(player, "downfall");
                        } else if (args[2].equalsIgnoreCase("reset")) {
                            player.resetPlayerWeather();
                            player.sendMessage(fileManager.getMessage("CustomVision.Weather", "reset").replace("&", "§"));
                        } else {
                            player.sendMessage("§7[§c彩虹工具§7] 错误, 参数 " + args[2] + " 不是有效的子命令参数: <clear/rain/reset>");
                        }
                        return true;

                    case "time":
                        if (args.length < 3 || args.length > 5) {
                            sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数过多或缺少必要参数");
                            sender.sendMessage("§7  |用法- §c/ru vs time day lock");
                            sender.sendMessage("§7  |用法- §c/ru vs time reset");
                            return true;
                        }
                        if (!sender.hasPermission("rainbowutility.use.time")) {
                            sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                            return true;
                        }

                        long time;
                        switch (args[2].toLowerCase()) {
                            case "morning":
                                time = 0;
                                break;
                            case "noon":
                                time = 6000;
                                break;
                            case "dusk":
                                time = 12000;
                                break;
                            case "night":
                                time = 18000;
                                break;
                            case "reset":
                                player.resetPlayerTime();
                                player.sendMessage(fileManager.getMessage("CustomVision.Time", "reset"));
                                return true;
                            default:
                                try {
                                    time = Long.parseLong(args[2]);
                                    if (time < 0 || time > 24000) {
                                        player.sendMessage("§7[§c彩虹工具§7] 错误, 参数 " + args[2] + " 不在可选区间内: [0-24000]");
                                        return true;
                                    }
                                } catch (NumberFormatException e) {
                                    player.sendMessage("§7[§c彩虹工具§7] 错误, 参数 " + args[2] + " 不是有效的预设值: <morning/noon/dusk/night/reset>");
                                    return true;
                                }
                        }

                        if (args[3].isEmpty()) {
                            player.sendMessage("§7[§c彩虹工具§7]  错误, 缺少必要参数: <lock/unlock>");
                            return true;
                        }

                        long difference = (time - player.getWorld().getTime() + 24000) % 24000;

                        if ("lock".equalsIgnoreCase(args[3])) {
                            player.setPlayerTime(time, false);
                            player.sendMessage(fileManager.getMessage("CustomVision.Time", "lock").replace("%rainbowutility_customvision_timelock%", Long.toString(time)));
                        } else if ("unlock".equalsIgnoreCase(args[3])) {
                            player.setPlayerTime(difference, true);
                            player.sendMessage(fileManager.getMessage("CustomVision.Time", "unlock").replace("%rainbowutility_customvision_timeunlock%", Long.toString(time)));
                        } else {
                            player.sendMessage("§7[§c彩虹工具§7] 错误, 参数" + args[3] + " 不是有效的状态类型: <lock/unlock>");
                        }
                }
                return true;

            case "admin":
                if (!sender.hasPermission("rainbowutility.operator")) {
                    sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                    return true;
                }

                if (args.length < 2) {
                    sender.sendMessage("§7[§c彩虹工具§7] 错误, 缺少必要参数: <reply/inbox/reject>");
                    return true;
                }

                String subCommandAdmin = args[1].toLowerCase();

                switch (subCommandAdmin) {
                    case "reply":
                        if (args.length < 4) {
                            sender.sendMessage("§7[§c彩虹工具§7] 错误, 缺少必要参数: <message>");
                            sender.sendMessage("§7  |用法- §c/ru reply JLING 您可以使用 /cd 打开菜单");
                            return true;
                        }

                        Player target = Bukkit.getServer().getPlayer(args[2]);
                        if (target != null) {
                            getHelp.replyToPlayer(player, target, Arrays.stream(args).skip(3).collect(Collectors.joining(" ")));
                        } else {
                            sender.sendMessage("§7[§c彩虹工具§7] 错误, 玩家 " + args[2] + "不在线");
                        }
                        break;

                    case "inbox":
                        List<String> inboxMessages = getHelp.viewInbox(player);
                        for (String msg : inboxMessages) {
                            player.sendMessage(msg.replace("&", "§"));
                        }
                        break;

                    case "reject":
                        if (args.length != 3) {
                            sender.sendMessage("§7[§c彩虹工具§7] 错误, 缺少必要参数 <true/false>");
                            sender.sendMessage("§7  |用法- §c/ru reject true");
                            return true;
                        }

                        boolean value = Boolean.parseBoolean(args[2]);
                        getHelp.toggleReject(player, value);
                        break;

                    default:
                        sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数 " + subCommandAdmin + " 不是有效的子命令参数: <reply/inbox/reject>");
                        break;
                }
                return true;

            case "reload":
                if (!sender.hasPermission("rainbowutility.reload")) {
                    sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                    return true;
                }
                fileManager.reloadConfig();
                sender.sendMessage("§7[§c彩虹工具§7] 配置文件已重新加载");
                break;

            case "help":
                if (args.length == 1) {
                    sender.sendMessage("§7[§c彩虹工具§7] 帮助:");
                    sender.sendMessage("§7必要参数: <?>, 可选必要参数: <?/?>, 非必要参数: [?], 可选非必要参数: [?/?]");
                    sender.sendMessage("§7  |- §c/ru tools §7- 查看工具包帮助");
                    sender.sendMessage("§7  |- §c/ru backto §7- 查看 §c快捷传送点 §7模块帮助");
                    sender.sendMessage("§7  |- §c/ru dropstop §7- 查看 §c丢弃保护 §7模块帮助");
                    sender.sendMessage("§7  |- §c/ru vision §7- 查看 §c自定义视觉 §7模块帮助");
                    sender.sendMessage("§7  |- §c/ru operator §7- 查看 §c管理员任务 §7模块帮助 (管理员)");
                    sender.sendMessage("§7  |- §c/ru reload §7- 重新载入配置文件 (管理员)");
                    return true;
                } else if (args.length == 2) {
                    switch (args[1].toLowerCase()) {
                        case "tools":
                            sender.sendMessage("§7[§c彩虹工具§7] 工具包 帮助:");
                            sender.sendMessage("§7必要参数: <?>, 可选必要参数: <?/?>, 非必要参数: [?], 可选非必要参数: [?/?]");
                            sender.sendMessage("§7  |- §c/ru cs [value] §7- 立即或延迟后清空聊天框");
                            sender.sendMessage("§7  |- §c/ru op <message> §true/false7- 向在线的管理员发出帮助请求");
                            sender.sendMessage("§7  |- §c/ru at <true/false> §7- 开启 或 关闭 接受高亮提示消息");
                            sender.sendMessage("§7  |- §c/ru fb <message> §7- 向服务器留言反馈");
                            break;
                        case "backto":
                            sender.sendMessage("§7[§c彩虹工具§7] 快捷传送点 帮助:");
                            sender.sendMessage("§7必要参数: <?>, 可选必要参数: <?/?>, 非必要参数: [?], 可选非必要参数: [?/?]");
                            sender.sendMessage("§7  |- §c/ru bk 或 /back §7- 返回上一个保存的传送点点");
                            sender.sendMessage("§7  |- §c/ru backset time <value> §7- 设置连续潜行保存传送点的判定时间");
                            sender.sendMessage("§7  |- §c/ru backset command <true/false> §7- 开启 或 关闭 对 /back 的响应");
                            break;
                        case "dropstop":
                            sender.sendMessage("§7[§c彩虹工具§7] 丢弃保护 帮助:");
                            sender.sendMessage("§7必要参数: <?>, 可选必要参数: <?/?>, 非必要参数: [?], 可选非必要参数: [?/?]");
                            sender.sendMessage("§7  |- §c/ru ds true §7- 立即开启丢弃保护");
                            sender.sendMessage("§7  |- §c/ru ds once [value] §7- 取消本次或指定次数的丢弃保护");
                            sender.sendMessage("§7  |- §c/ru ds day [value] §7- 取消本日或指定日数的丢弃保护");
                            sender.sendMessage("§7  |- §c/ru ds keep §7- 永久取消丢弃保护");
                            break;
                        case "vision":
                            sender.sendMessage("§7[§c彩虹工具§7] 自定义视觉 帮助:");
                            sender.sendMessage("§7必要参数: <?>, 可选必要参数: <?/?>, 非必要参数: [?], 可选非必要参数: [?/?]");
                            sender.sendMessage("§7  |- §c/ru weather <clear/rain> §7- 修改个人视角的天气");
                            sender.sendMessage("§7  |- §c/ru weather reset §7- 将个人视角的天气还原至全局设置");
                            sender.sendMessage("§7  |- §c/ru time <morning/noon/dusk/night> <lock/unlock> §7- 修改个人视角的时间");
                            sender.sendMessage("§7  |- §c/ru time reset §7- 将个人视角的时间还原至全局设置");
                            break;
                        case "operator":
                            if (!sender.hasPermission("rainbowutility.operator")) {
                                sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
                                return true;
                            }
                            sender.sendMessage("§7[§c彩虹工具§7] 管理员任务 帮助:");
                            sender.sendMessage("§7必要参数: <?>, 可选必要参数: <?/?>, 非必要参数: [?], 可选非必要参数: [?/?]");
                            sender.sendMessage("§7  |- §c/ru reply <playerName> <message> §7- 回复指定玩家的求助");
                            sender.sendMessage("§7  |- §c/ru reject <true/false> §7- 开启 或 关闭玩家求助提示");
                            sender.sendMessage("§7  |- §c/ru inbox §7- 查看玩家求助收件箱");
                            break;
                        default:
                            sender.sendMessage("§7[§c彩虹工具§7] 错误, 参数 " + args[1].toLowerCase() + " 不是有效的子命令参数: <tools/bakto/dropstop/vision/operator>");
                            return true;
                    }
                    return true;
                }
            default:
                sender.sendMessage("§7[§c彩虹工具§7] 这是一个不存在的命令或拼写错误: " + args[0]);
                sender.sendMessage("§7  |- 输入 §c/ru help §7查看帮助");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("cs", "op", "at", "fb", "bk", "ds", "vs", "admin", "help"));
        } else {
            switch (args[0].toLowerCase()) {
                case "cs":
                    if(args.length == 2) completions.add("[整数数字]");
                    break;
                case "op":
                    if(args.length == 2) completions.add("<您想要咨询的内容>");
                    break;
                case "at":
                    if(args.length == 2) completions.addAll(Arrays.asList("true", "false"));
                    break;
                case "fb":
                    if(args.length == 2) completions.add("<您想要反馈的内容>");
                    break;
                case "bk":
                    if (args.length == 2) {
                        completions.add("set");
                    } else if (args[1].equalsIgnoreCase("set") && args.length == 3) {
                        completions.addAll(Arrays.asList("command", "info", "time"));
                    } else if (args[1].equalsIgnoreCase("set") && args[2].equalsIgnoreCase("info") && args.length == 4) {
                        completions.addAll(Arrays.asList("title", "chat", "actionbar"));
                    } else if (args[1].equalsIgnoreCase("set") && args[2].equalsIgnoreCase("time") && args.length == 4) {
                        completions.add("[整数数字]");
                    } else if (args[1].equalsIgnoreCase("set") && args[2].equalsIgnoreCase("command") && args.length == 4) {
                        completions.addAll(Arrays.asList("true", "false"));
                    }
                    break;
                case "ds":
                    if (args.length == 2) {
                        completions.addAll(Arrays.asList("once", "day", "keep", "true"));
                    } else if ((args[1].equalsIgnoreCase("once") || args[1].equalsIgnoreCase("day")) && args.length == 3) {
                        completions.add("[整数数字]");
                    }
                    break;
                case "vs":
                    if (args.length == 2) {
                        completions.addAll(Arrays.asList("weather", "time"));
                    } else if (args[1].equalsIgnoreCase("weather") && args.length == 3) {
                        completions.addAll(Arrays.asList("clear", "rain", "reset"));
                    } else if (args[1].equalsIgnoreCase("time") && args.length == 3) {
                        completions.addAll(Arrays.asList("[整数数字]", "morning", "noon", "dusk", "night", "reset", "value"));
                    } else if (args[1].equalsIgnoreCase("time") && !args[2].equalsIgnoreCase("reset") && args.length == 4) {
                        completions.addAll(Arrays.asList("lock", "unlock"));
                    }
                    break;
                case "admin":
                    if (args.length == 2) {
                        completions.addAll(Arrays.asList("reply", "reject", "inbox"));
                    } else if (args[1].equalsIgnoreCase("reply") && args.length == 3) {
                        completions.addAll(getHelp.getInboxKeys().stream().map(Player::getName).toList());
                    } else if (args[1].equalsIgnoreCase("reply") && args.length == 4) {
                        completions.add("<您的回复>");
                    } else if (args[1].equalsIgnoreCase("reject") && args.length == 3) {
                        completions.addAll(Arrays.asList("true", "false"));
                    }
                    break;
                case "help":
                    if(args.length == 2) completions.addAll(Arrays.asList("tools", "back", "dropstop", "vision", "operator"));
                    break;
            }
        }

        StringUtil.copyPartialMatches(args[args.length - 1], completions, results);

        return completions;
    }

    private boolean handleBackCommand(Player player, CommandSender sender) {
        if (player == null) {
            Bukkit.getServer().getLogger().info("[彩虹工具] 此命令必须由一个玩家执行");
            return true;
        }
        if (!sender.hasPermission("rainbowutility.use.back")) {
            sender.sendMessage("§7[§c彩虹工具§7] 您没有执行此命令的权限");
            return true;
        }

        Location savedLoc = BackTo.getSavedLocation(player);
        if (savedLoc != null) {
            player.teleport(savedLoc);
            player.sendMessage(fileManager.getMessage("BackTo", "successTeleport"));
        } else {
            player.sendMessage(fileManager.getMessage("BackTo", "noSavePosition"));
        }
        return true;
    }

}