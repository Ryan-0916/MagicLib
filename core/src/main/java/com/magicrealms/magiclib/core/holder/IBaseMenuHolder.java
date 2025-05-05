package com.magicrealms.magiclib.core.holder;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * @author Ryan-0916
 * @Desc 使用箱子伪造菜单对象相关接口
 * 该接口定义了与菜单容器相关的事件处理方法，通常用于伪造菜单或自定义的 UI 界面（例如使用 Minecraft 中的箱子或其他容器实现）。
 * 通过实现此接口，可以处理玩家与菜单的交互事件，如点击、拖拽、打开和关闭菜单等。
 * @date 2024-04-11
 */
@SuppressWarnings("unused")
public interface IBaseMenuHolder {

    /**
     * 点击菜单顶部容器事件
     * 当玩家点击菜单的顶部区域（例如顶部的栏位或按钮）时触发此事件。
     * 该方法用于处理玩家在顶部区域的交互行为，通常用来执行按钮或选项的功能。
     * @param event 触发的事件对象，包含了点击的信息。
     */
    default void topInventoryClickEvent(InventoryClickEvent event, int slot) {}

    /**
     * 拖拽菜单容器事件
     * 当玩家在菜单中拖动物品时触发此事件。用于处理拖拽物品的行为，例如改变物品位置或执行其他自定义操作。
     * @param event 触发的拖拽事件对象，包含了拖拽的详细信息。
     */
    default void dragEvent(InventoryDragEvent event) {}

    /**
     * 打开菜单容器事件
     * 当玩家打开菜单容器时触发此事件。通常用于初始化菜单内容、更新菜单界面或执行其他在打开时需要进行的操作。
     * @param event 触发的打开事件对象，包含了打开容器的相关信息。
     */
    default void openEvent(InventoryOpenEvent event) {}

    /**
     * 关闭菜单容器事件
     * 当玩家关闭菜单容器时触发此事件。可以用来清理资源、保存数据或执行其他关闭时需要的操作。
     * @param event 触发的关闭事件对象，包含了关闭容器的相关信息。
     */
    default void closeEvent(InventoryCloseEvent event) {}

    /**
     * 点击菜单底部容器事件
     * 当玩家点击菜单的底部区域（例如底部的物品栏位）时触发此事件。
     * 该方法用于处理玩家在底部区域的交互行为，通常用来执行物品栏操作或其他功能。
     * @param event 触发的点击事件对象，包含了点击的信息。
     */
    default void bottomInventoryClickEvent(InventoryClickEvent event, int slot) {}
}
