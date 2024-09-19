package com.magicrealms.magiclib.common.dispatcher;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc NMS 调度器
 * @date 2024-07-17
 */
public interface INMSDispatcher {

    /**
     * 发包修改正在打开的容器 Title
     * @param player 玩家
     * @param title 标题 - 支持 MiniMessage
     */
    void updateInventoryTitle(@NotNull Player player, @NotNull String title);

    /**
     * 发包打开铁砧并修改 Title
     * @param player 玩家
     * @param anvilItems 铁砧物品
     * @param title 标题 - 支持 MiniMessage
     * @return 铁砧视图 {@link InventoryView}
     */
    InventoryView openAnvil(@NotNull Player player, @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title);

    /**
     * 设置玩家正在打开的铁砧第一项与 Title
     * @param player 玩家
     * @param anvilItems 铁砧物品
     * @param title 标题 - 支持 MiniMessage
     */
    void setupAnvil(@NotNull Player player,
                    @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title);
}
