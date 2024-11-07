package com.magicrealms.magiclib.common.dispatcher;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc NMS 调度器接口，用于处理 MagicRealms 插件中与玩家界面相关的 NMS 操作
 * @date 2024-07-17
 */
public interface INMSDispatcher {

    /**
     * 发送网络消息以修改玩家正在打开的容器（如背包、 chest等）的标题
     * @param player 玩家对象，指定要修改界面标题的玩家
     * @param title  新的标题字符串，支持 MiniMessage 格式以进行动态文本渲染
     */
    void updateInventoryTitle(@NotNull Player player, @NotNull String title);

    /**
     * 发送网络消息以打开铁砧并设置其标题
     * @param player 玩家对象，指定要为其打开铁砧的玩家
     * @param anvilItems 铁砧中展示的物品映射，键为物品槽位，值为物品堆栈
     * @param title 铁砧的标题字符串，支持 MiniMessage 格式
     * @return 返回打开的铁砧视图对象，可用于进一步的界面操作
     */
    InventoryView openAnvil(@NotNull Player player, @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title);

    /**
     * 设置玩家正在打开的铁砧的物品与标题（通常用于初始化或更新铁砧界面）
     * 注意：此方法可能与 openAnvil 方法结合使用，先打开铁砧，然后设置第一项与标题
     * 或者在某些情况下，用于更新已经打开的铁砧界面。
     * @param player     玩家对象，指定要设置铁砧项与标题的玩家
     * @param anvilItems 铁砧中展示的物品映射，键为物品槽位，值为物品堆栈
     * @param title 铁砧的标题字符串，支持 MiniMessage 格式
     */
    void setupAnvil(@NotNull Player player,
                    @NotNull Map<Integer, ItemStack> anvilItems, @NotNull String title);
}
