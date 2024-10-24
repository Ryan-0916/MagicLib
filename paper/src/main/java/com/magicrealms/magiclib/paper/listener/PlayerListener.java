package com.magicrealms.magiclib.paper.listener;

import com.magicrealms.magiclib.common.utils.ItemUtil;
import com.magicrealms.magiclib.paper.holder.BaseMenuHolder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 玩家监听器
 * @date 2024-05-08
 */
@SuppressWarnings("unused")
public class PlayerListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e){
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            e.setCancelled(menu.isLock());
            int clickedSlot = e.getRawSlot();
            ItemStack clickedItem = e.getCurrentItem();

            /*
            * UPDATE BY Ryan0916 - 2024-07-06
            * Remark: 添加上锁的箱子点击底部进入事件
            *
            if (clickedSlot < 0 || (menu.isLock() && (ItemUtil.isAirOrNull(clickedItem)
                    || clickedSlot >= menu.getLayout().length()
                    || clickedSlot >= e.getInventory().getSize()))) {
                return;
            }
            */

            if (clickedSlot < 0) {
                return;
            }

            if (clickedSlot >= menu.getLayout().length() ||
                    clickedSlot >= e.getInventory().getSize()) {
                menu.clickBottomSlotEvent(e, clickedSlot);
                return;
            }

            /* 如果箱子上锁了，点击了空的地方无需做任何处理 */
            if (menu.isLock() && ItemUtil.isAirOrNull(clickedItem)) {
                return;
            }

            menu.clickSlotEvent(e, clickedSlot);
        }
    }

    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e){
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            if (menu.isLock()) {
                return;
            }
            menu.dragEvent(e);
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e){
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            menu.closeEvent(e);
        }
    }

    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            menu.openEvent(e);
        }
    }

}
