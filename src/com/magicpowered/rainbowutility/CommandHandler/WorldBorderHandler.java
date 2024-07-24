package com.magicpowered.rainbowutility.CommandHandler;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldBorderHandler {

    public WorldBorderHandler() {}

    private org.bukkit.WorldBorder getWorldBorder(CommandSender sender, String worldName) {
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            sender.sendMessage("§7[§6彩虹工具§7] §c错误, 指定世界 " + worldName + " 不存在");
            return null;
        }

        return world.getWorldBorder();
    }

    public boolean handleWbWarnCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <get|set|time>");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <world>");
            return true;
        }

        String worldName = args[3];
        org.bukkit.WorldBorder wb = getWorldBorder(sender, worldName);
        if (wb == null) return true;

        switch (args[2].toLowerCase()) {
            case "get":
                sender.sendMessage("§7[§6彩虹工具§7] §a世界 " + worldName + " 接近边界得到警告的方块数为 " + wb.getWarningDistance());
                break;
            case "set":
                if (args.length < 5) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <num>");
                    return true;
                }
                int warnSetNum;
                try {
                    warnSetNum = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + "不是数字");
                    return true;
                }
                sender.sendMessage("§7[§6彩虹工具§7] §a设置世界 " + worldName + " 接近边界得到警告的方块数为 " + warnSetNum);
                wb.setWarningDistance(warnSetNum);
                break;
            case "time":
                if (args.length < 5) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <num>");
                    return true;
                }
                int warnTimeNum;
                try {
                    warnTimeNum = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + "不是数字");
                    return true;
                }
                sender.sendMessage("§7[§6彩虹工具§7] §a设置世界 " + worldName + " 接近边界得到的警告时间为 " + warnTimeNum);
                wb.setWarningTime(warnTimeNum);
                break;
            default:
                sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[2].toLowerCase() + " 不是有效的子命令参数: <get/set/time>");
                return true;
        }

        return true;
    }

    public boolean handleWbNormalCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <get|set|time>");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <world>");
            return true;
        }

        String worldName = args[3];
        org.bukkit.WorldBorder wb = getWorldBorder(sender, worldName);
        if (wb == null) return true;

        String world = args[3];
        switch (args[2].toLowerCase()) {
            case "info":
                sender.sendMessage("§7[§6彩虹工具§7] §a世界 " + world + " 的边界信息为: ");
                sender.sendMessage("§7  |- §f中心: " + wb.getCenter());
                sender.sendMessage("§7  |- §f长度: " + wb.getSize());
                break;
            case "set":
                if (args.length < 5) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <num>");
                    return true;
                }
                int setNum;
                try {
                    setNum = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + "不是数字");
                    return true;
                }
                sender.sendMessage("§7[§6彩虹工具§7] §a设置世界 " + world + " 的边界为 " + setNum);
                break;
            case "add":
                if (args.length < 5) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <num>");
                    return true;
                }
                double addNum, newSizeAdd;
                try {
                    addNum = Double.parseDouble(args[4]);
                    newSizeAdd = wb.getSize() + addNum;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + "不是数字");
                    return true;
                }
                sender.sendMessage("§7[§6彩虹工具§7] §a将世界 " + worldName + " 的边界增加 " + addNum + " ( " + wb.getSize() + " -> " + newSizeAdd + " )");
                wb.setSize(newSizeAdd);
                break;
            case "reduce":
                if (args.length < 5) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <num>");
                    return true;
                }
                double reduceNum, newSizeReduce;
                try {
                    reduceNum = Double.parseDouble(args[4]);
                    newSizeReduce = wb.getSize() - reduceNum;
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + "不是数字");
                    return true;
                }
                if (newSizeReduce < 0) newSizeReduce = 0;
                sender.sendMessage("§7[§6彩虹工具§7] §a将世界 " + worldName + " 的边界减少 " + reduceNum + " （ " + wb.getSize() + " —> " + newSizeReduce + " )");
                wb.setSize(newSizeReduce);
                break;
            case "center":
                if (args.length < 5) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage("[彩虹工具] 此命令必须由一个玩家执行");
                        sender.sendMessage("[彩虹工具] 如果您希望从控制台设置，请指定坐标位置 x, z");
                        return true;
                    }
                    sender.sendMessage("§7[§6彩虹工具§7] §a将世界 " + worldName + " 的边界中心设置为当前位置");
                    wb.setCenter(((Player) sender).getLocation());
                    return true;
                } else if (args.length == 6) {
                    int x, z;
                    try {
                        x = Integer.parseInt(args[4]);
                        z = Integer.parseInt(args[5]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + " " + args[5] + " 中有一位不是数字");
                        return true;
                    }
                    sender.sendMessage("§7[§6彩虹工具§7] §a将世界 " + worldName + " 的边界中心设置为 (" + x + ", " + z + ")");
                    return true;
                } else {
                    sender.sendMessage("§7[§6彩虹工具§7] §c参数过多或缺少必要参数");
                }
                break;
            case "reset":
                sender.sendMessage("§7[§6彩虹工具§7] §a世界 " + worldName + " 边界恢复默认值");
                break;
            default:
                sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[2].toLowerCase() + " 不是有效的子命令参数: <info/set/add/reduce/center/reset>");
                return true;
        }

        return true;
    }

    public boolean handleWbDamageCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <get|set|time>");
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <world>");
            return true;
        }

        String worldName = args[3];
        org.bukkit.WorldBorder wb = getWorldBorder(sender, worldName);
        if (wb == null) return true;

        switch (args[2].toLowerCase()) {
            case "get":
                sender.sendMessage("§7[§6彩虹工具§7] §a世界 " + worldName + " 越过边界后受到的伤害值为 " + wb.getDamageAmount());
                break;
            case "set":
                if (args.length < 5) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c缺少必要参数: <num>");
                    return false;
                }
                int damageSetNum;
                try {
                    damageSetNum = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + "不是数字");
                    return true;
                }
                sender.sendMessage("§7[§6彩虹工具§7] §a设置世界 " + worldName + " 越过边界后受到的伤害值为 " + damageSetNum);
                wb.setDamageAmount(damageSetNum);
                break;
            case "buffer":
                if (args.length < 5) {
                    sender.sendMessage("§7[§6彩o n g虹工具§7] §c缺少必要参数: <num>");
                    return false;
                }
                int bufferNum;
                try {
                    bufferNum = Integer.parseInt(args[4]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[4] + "不是数字");
                    return true;
                }
                sender.sendMessage("§7[§6彩虹工具§7] §a设置世界 " + worldName + " 越过边界后仍安全的方块数为 " + bufferNum);
                wb.setDamageBuffer(bufferNum);
                break;
            default:
                sender.sendMessage("§7[§6彩虹工具§7] §c错误, 参数 " + args[2].toLowerCase() + " 不是有效的子命令参数: <get/set/buffer>");
                return false;
        }

        return true;
    }
}
