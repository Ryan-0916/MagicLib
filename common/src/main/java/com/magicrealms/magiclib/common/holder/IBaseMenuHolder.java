package com.magicrealms.magiclib.common.holder;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 菜单构造器-接口
 * @author Ryan-0916
 * @date 2024-04-11
 */
public interface IBaseMenuHolder {

    void clickSlotEvent(@NotNull InventoryClickEvent e, int clickedSlot);

    void dragEvent(@NotNull InventoryDragEvent e);

    void openEvent(@NotNull InventoryOpenEvent e);

    void closeEvent(@NotNull InventoryCloseEvent e);

    void clickBottomSlotEvent(@NotNull InventoryClickEvent e, int clickedSlot);

    void closeMenu();
}
