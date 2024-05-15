package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.RainbowUtility;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class GetHelp {
    private RainbowUtility plugin;
    private FileManager fileManager;
    private HashMap<Player, Player> replyMap = new HashMap<>();
    private Map<Player, ArrayList<String>> inboxMap = new HashMap<>();
    private HashMap<Player, Boolean> rejectMap = new HashMap<>();


    public GetHelp(RainbowUtility plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;

        for(Player op : plugin.getServer().getOnlinePlayers()) {
            if(op.isOp()) {
                boolean rejectValue = fileManager.getDatabase().getBoolean("GetHelp." + op.getUniqueId().toString() + ".reject", false);
                rejectMap.put(op, rejectValue);
            }
        }
    }

    public Set<Player> getInboxKeys() {
        return inboxMap.keySet();
    }

    public void sendOpHelp(Player sender, String message) {
        for (Player op : plugin.getServer().getOnlinePlayers()) {
            if (op.isOp() && !shouldReject(op)) {
                op.sendTitle("§6求助", "来自 " + sender.getName(), 10, 60, 10);
                op.sendMessage("§7[§6彩虹工具§7] 玩家 " + sender.getName() + " 发起求助: " + message);

                ArrayList<String> messagesForOp = inboxMap.getOrDefault(op, new ArrayList<>());  // Get existing list or create new
                messagesForOp.add(sender.getName() + ":" + message);  // Add new message
                inboxMap.put(op, messagesForOp);  // Update map
            }
        }
        replyMap.put(sender, null);
    }


    public void replyToPlayer(Player op, Player target, String message) {
        if (replyMap.containsKey(target) && replyMap.get(target) == null) {
            String template = fileManager.getConfig().getString("GetHelp.opReply", "§7[§6彩虹工具§7] 管理员 %gethelp_replayop% 回复了您: %gethelp_replymsg%");

            String formattedMessage = template
                    .replace("%gethelp_replayop%", op.getName())
                    .replace("%gethelp_replymsg%", message);

            target.sendMessage(formattedMessage);
            op.sendMessage("§7[§6彩虹工具§7] 成功向 " + target.getName() + " 回复消息: " + formattedMessage);
            replyMap.put(target, op);

            // Remove the player's message from the inboxMap after replying
            if(inboxMap.containsKey(op)) {
                inboxMap.get(op).removeIf(msg -> msg.startsWith(target.getName() + ":"));
            }
        } else {
            op.sendMessage("§7[§6彩虹工具§7] 该玩家尚未发起求助, 或他的求助已被其他管理员回应");
        }
    }


    public ArrayList<String> viewInbox(Player op) {
        ArrayList<String> messages = new ArrayList<>();
        if (inboxMap.containsKey(op)) {
            messages.add("§7[§6彩虹工具§7] 收件箱:");
            for (String message : inboxMap.get(op)) {
                String[] parts = message.split(":");  // Assuming the format is "PlayerName:Message"
                if (parts.length >= 2) {
                    messages.add("&7 |&c" + parts[0] + "&7- &f" + parts[1]);
                }
            }
        }
        return messages;
    }


    public void toggleReject(Player op, boolean value) {
        rejectMap.put(op, value);

        // Save the reject status to the database
        fileManager.getDatabase().set("GetHelp." + op.getUniqueId() + ".reject", value);
        fileManager.saveDatabase();

        op.sendMessage("§7[§6彩虹工具§7] 成功将求助消息提示设置为: " + value);
    }

    public void sendFeedback(Player player, String message) {
        // 获取当前日期和时间，并按照指定格式进行格式化
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String key = player.getName() + "." + currentTime + " 在 " + player.getWorld().getName() + " 世界";

        // 存储消息
        fileManager.getFeedback().set(key, message);
        fileManager.saveFeedback();
        player.sendMessage(fileManager.getMessage("FeedBack", "successSend"));
    }


    private boolean shouldReject(Player op) {
        return fileManager.getDatabase().getBoolean("GetHelp." + op.getUniqueId() + ".reject", false);
    }

}