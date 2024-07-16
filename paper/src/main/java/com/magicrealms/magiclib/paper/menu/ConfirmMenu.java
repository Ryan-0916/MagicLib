package com.magicrealms.magiclib.paper.menu;

import com.magicrealms.magiclib.paper.MagicLib;
import com.magicrealms.magiclib.paper.holder.BaseMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.magicrealms.magiclib.paper.MagicLib.YML_CONFIRM_MENU;

/**
 * @author Ryan-0916
 * @Desc 确认菜单
 * @date 2024-07-16
 */
@SuppressWarnings("unused")
public class ConfirmMenu extends BaseMenuHolder {
    private final ItemStack showItem;
    private final Runnable confirmRun;
    private final Runnable cancelOrCloseRun;
    private boolean manualClose;

    private ConfirmMenu(Builder builder) {
        super(MagicLib.getInstance(), builder.player, YML_CONFIRM_MENU, "###ABC###");
        this.showItem = builder.showItem;
        this.confirmRun = builder.confirmRun;
        this.cancelOrCloseRun = builder.cancelOrCloseRun;
        openMenu();
    }

    private void setMenu() {
        int size =  super.getLayout().length();
        for (int i = 0; i < size; i++){
            if (super.getLayout().charAt(i) == 'B') {
                super.setItemSlot(i, showItem);
                continue;
            }
            super.setItemSlot(i);
        }
    }

    private void openMenu() {
        setMenu();
        super.getPlayer().openInventory(super.getInventory());
    }

    @Override
    public void clickSlotEvent(@NotNull InventoryClickEvent e, int clickedSlot) {
        char c = super.getLayout().charAt(clickedSlot);
        Player whoClicked = (Player) e.getWhoClicked();
        super.playSound("Icons." + c + ".display.sound");
        switch (c) {
            case 'A':
                manualClose = false;
                clickButton(whoClicked, true);
                break;
            case 'C':
                manualClose = false;
                clickButton(whoClicked, false);
                break;
        }
    }

    private void clickButton(Player player, boolean isConfirm) {
        Bukkit.getScheduler().runTask(MagicLib.getInstance(), () -> {
            player.closeInventory();
            if (isConfirm) {
                confirmRun.run();
                return;
            }
            cancelOrCloseRun.run();
        });
    }

    @Override
    public void openEvent(@NotNull InventoryOpenEvent e) {
        super.openEvent(e);
        e.titleOverride(super.getTitle(new HashMap<>()));
        manualClose = true;
    }

    @Override
    public void closeEvent(@NotNull InventoryCloseEvent e) {
        super.closeEvent(e);
        Bukkit.getScheduler().runTask(MagicLib.getInstance(), () -> {
            if (manualClose) {
                cancelOrCloseRun.run();
            }
        });
    }

    public static class Builder {
        private ItemStack showItem;
        private Runnable confirmRun;
        private Runnable cancelOrCloseRun;
        private Player player;

        public Builder showItem(@NotNull ItemStack showItem) {
            this.showItem = showItem;
            return this;
        }

        public Builder confirmRun(@NotNull Runnable confirmRun) {
            this.confirmRun = confirmRun;
            return this;
        }

        public Builder cancelOrCloseRun(@NotNull Runnable cancelOrCloseRun) {
            this.cancelOrCloseRun = cancelOrCloseRun;
            return this;
        }

        public Builder player(@NotNull Player player) {
            this.player = player;
            return this;
        }

        public void open() {
            if (player == null) throw new NullPointerException("构建确认菜单时出现未知异常，确认者不可为空");
            if (showItem == null) throw new NullPointerException("构建确认菜单时出现未知异常，展示物品不可为空");
            if (confirmRun == null) throw new NullPointerException("构建确认菜单时出现未知异常，确认后的操作不可为空");
            if (cancelOrCloseRun == null) throw new NullPointerException("构建确认菜单时出现未知异常，确认或取消后的不可为空");
            new ConfirmMenu(this);
        }
    }
}
