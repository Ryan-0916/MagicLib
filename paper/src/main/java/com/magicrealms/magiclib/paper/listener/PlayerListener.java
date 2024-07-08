package com.magicrealms.magiclib.paper.listener;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
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
 **/
public class PlayerListener implements Listener {

    private final MagicRealmsPlugin PLUGIN;

    public PlayerListener(MagicRealmsPlugin plugin) {
        this.PLUGIN = plugin;
    }

    /**
     * 当玩家点击物品栏/菜单触发本事件
     * @param e 玩家点击菜单事件
     */
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

    /**
     * 玩家滑动物品/菜单触发本事件
     * @param e 玩家滑动菜单事件
     */
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e){
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            if (menu.isLock()) {
                return;
            }
            menu.dragEvent(e);
        }
    }

    /**
     * 当玩家关闭物品栏/菜单触发本事件
     * @param e 玩家关闭菜单事件
     */
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent e){
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            menu.closeEvent(e);
        }
    }

    /**
     * 当玩家打开物品/菜单触发本事件
     * @param e 玩家开启菜单事件
     */
    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof BaseMenuHolder menu) {
            menu.openEvent(e);
        }
    }
}
