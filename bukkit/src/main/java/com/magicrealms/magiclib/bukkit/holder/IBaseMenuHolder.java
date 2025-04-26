package com.magicrealms.magiclib.bukkit.holder;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * 菜单构造器-接口
 * @author Ryan-0916
 * @date 2024-04-11
 */
public interface IBaseMenuHolder {

    void clickSlotEvent(InventoryClickEvent e, int clickedSlot);

    void dragEvent(InventoryDragEvent e);

    void openEvent(InventoryOpenEvent e);

    void closeEvent(InventoryCloseEvent e);

    void clickBottomSlotEvent(InventoryClickEvent e, int clickedSlot);

    void closeMenu();
}
