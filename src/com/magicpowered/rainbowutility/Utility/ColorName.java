package com.magicpowered.rainbowutility.Utility;

import com.magicpowered.rainbowutility.FileManager;
import com.magicpowered.rainbowutility.Storage.Message;
import com.magicpowered.rainbowutility.RainbowUtility;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ColorName implements Listener {
    RainbowUtility plugin;
    FileManager fileManager;
    ColorNameCard colorNameCard;
    Message messages;
    int colorNameCost;

    public ColorName(RainbowUtility plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
        reloadColorNameCardConfig();
    }

    public void reloadColorNameCardConfig() {
        FileConfiguration config = fileManager.getConfig();
        this.colorNameCost = config.getInt("ColorName.Vouchers.cost");

        this.colorNameCard = new ColorNameCard(
                Material.getMaterial(config.getString("ColorName.Vouchers.material", "paper")),
                config.getString("ColorName.Vouchers.name", "彩色命名卡"),
                config.getStringList("ColorName.Vouchers.lore")
        );
    }

    public void givePlayerColorNameCard(Player player, int num) {
        int maxStackSize = colorNameCard.material.getMaxStackSize();
        int fullStacks = num / maxStackSize;
        int remainingItems = num % maxStackSize;

        ItemStack colorNameCardItem = new ItemStack(colorNameCard.material);
        ItemMeta meta = colorNameCardItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(colorNameCard.name);
            meta.setLore(colorNameCard.lore);
            colorNameCardItem.setItemMeta(meta);
        }

        for (int i = 0; i < fullStacks; i++) {
            ItemStack stack = colorNameCardItem.clone();
            stack.setAmount(maxStackSize);
            player.getInventory().addItem(stack);
        }

        if (remainingItems > 0) {
            ItemStack stack = colorNameCardItem.clone();
            stack.setAmount(remainingItems);
            player.getInventory().addItem(stack);
        }

        player.sendMessage(fileManager.getMessage("ColorName", "Message.giveCard").replace("%amount%", String.valueOf(num)));
    }

    public void setColorNameByCard(Player player, String changedName) {
        ItemStack mainHandItem = player.getInventory().getItemInMainHand();

        if (mainHandItem.getType() == Material.AIR) {
            player.sendMessage(fileManager.getMessage("ColorName", "Message.noItem"));
            return;
        }

        ItemMeta meta = mainHandItem.getItemMeta();
        if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(colorNameCard.name) && meta.getLore().equals(colorNameCard.lore)) {
            meta.setDisplayName(changedName);
            mainHandItem.setItemMeta(meta);
            player.getInventory().remove(mainHandItem);
            player.sendMessage(fileManager.getMessage("ColorName", "Message.success"));
            return;
        }

        player.sendMessage(fileManager.getMessage("ColorName", "Message.noCard"));
    }

    static class ColorNameCard{
        private final Material material;
        private final String name;
        private final List<String> lore;
        public ColorNameCard(Material material, String name, List<String> lore) {
            this.material = material;
            this.name = name;
            this.lore = lore;
        }
    }
}
