package com.magicrealms.magiclib.core.dispatcher;

import com.magicrealms.magiclib.bukkit.dispatcher.INMSDispatcher;
import com.magicrealms.magiclib.common.exception.UnsupportedVersionException;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;


import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc NMS 调度器
 * @date 2024-07-17
 */
public class NMSDispatcher implements INMSDispatcher {

    private static volatile NMSDispatcher INSTANCE;

    private final INMSDispatcher NMS_DISPATCHER;

    private NMSDispatcher() {
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        String packageName;
        switch (bukkitVersion) {
            case "1.21.4" -> packageName = "mc_1_21_R3";
            case "1.20.3", "1.20.4" -> packageName = "mc_1_20_R3";
            case "1.20", "1.20.1" -> packageName = "mc_1_20_R1";
            default -> throw new UnsupportedVersionException("您的 Minecraft 版本不兼容，请使用合适的版本");
        }
        try {
            Class<?> clazz = Class.forName("com.magicrealms.magiclib." + packageName + ".dispatcher." + StringUtils.upperCase(packageName) + "_NMSDispatcher");
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            NMS_DISPATCHER = (INMSDispatcher) constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("MagicLib 初始化失败", e);
        }
    }

    public static NMSDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized(NMSDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NMSDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void openCustomInventory(Player player, Inventory inventory, String title) {
        NMS_DISPATCHER.openCustomInventory(player, inventory, title);
    }

    @Override
    public void updateInventoryTitle(Player player, String title) {
        NMS_DISPATCHER.updateInventoryTitle(player, title);
    }

    @Override
    public InventoryView openAnvil(Player player,
                                   Map<Integer, ItemStack> anvilItems, String title) {
        return NMS_DISPATCHER.openAnvil(player, anvilItems, title);
    }

    @Override
    public void setupAnvil(Player player,
                           Map<Integer, ItemStack> anvilItems, String title) {
        NMS_DISPATCHER.setupAnvil(player, anvilItems, title);
    }

    @Override
    public void resetChatDialog(Player player, List<String> messageHistory) {
        NMS_DISPATCHER.resetChatDialog(player, messageHistory);
    }

    @Override
    public void playSound(Player player, String namespace, float volume, float pitch, long speed) {
        NMS_DISPATCHER.playSound(player, namespace, volume, pitch, speed);
    }

    @Override
    public void setItemCooldown(Player player, ItemStack item, int duration) {
        NMS_DISPATCHER.setItemCooldown(player, item, duration);
    }

    @Override
    public void removeItemCooldown(Player player, ItemStack item) {
        NMS_DISPATCHER.removeItemCooldown(player, item);
    }

}
