package com.magicrealms.magiclib.core.listener;

import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import com.magicrealms.magiclib.core.holder.BaseMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
                menu.bottomInventoryClickEvent(e, clickedSlot);
                return;
            }

            /* 如果箱子上锁了，点击了空的地方无需做任何处理 */
            if (menu.isLock() && ItemUtil.isAirOrNull(clickedItem)) {
                return;
            }

            menu.topInventoryClickEvent(e, clickedSlot);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDragEvent(InventoryDragEvent e){
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            if (menu.isLock()) {
                return;
            }
            menu.dragEvent(e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryCloseEvent(InventoryCloseEvent e){
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            menu.closeEvent(e);
            HumanEntity player = e.getPlayer();
            Bukkit.getScheduler().runTask(MagicLib.getInstance(), () -> {
                /* 防止客户端延迟接收导致的 UpdateTitle 再次开启了容器，因此需要二次关闭菜单 */
                if (e.getReason() == InventoryCloseEvent.Reason.OPEN_NEW ||
                    player.getOpenInventory().getTopInventory().getHolder() instanceof BaseMenuHolder) {
                    return;
                }
                player.closeInventory();
            });
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            menu.openEvent(e);
        }
    }

}
