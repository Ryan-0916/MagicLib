package com.magicrealms.magiclib.core.menu;

import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.holder.BaseMenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import static com.magicrealms.magiclib.core.MagicLibConstant.YML_CONFIRM_MENU;

/**
 * @author Ryan-0916
 * @Desc 确认菜单
 * @date 2024-07-16
 */
@SuppressWarnings("unused")
public class ConfirmMenu extends BaseMenuHolder {
    private final ItemStack ITEM_STACK;
    private final Runnable CONFIRM_RUNNABLE;
    private final Runnable CANCEL_RUNNABLE;
    private final Runnable CLOSE_RUNNABLE;
    /* 主动关闭 */
    private boolean manualClose;

    private ConfirmMenu(Builder builder) {
        super(MagicLib.getInstance(),
                builder.player,
                YML_CONFIRM_MENU,
                "###ABC###");
        this.ITEM_STACK = builder.itemStack;
        this.CONFIRM_RUNNABLE = builder.confirmRunnable;
        this.CANCEL_RUNNABLE = builder.cancelRunnable;
        this.CLOSE_RUNNABLE = builder.closeRunnable;
        super.asyncOpenMenu();
    }

    @Override
    protected void handleMenu(String layout) {
        int size = layout.length();
        for (int i = 0; i < size; i++){
            if (layout.charAt(i) == 'B') {
                super.setItemSlot(i, ITEM_STACK);
                continue;
            }
            super.setItemSlot(i);
        }
    }

    @Override
    protected String handleTitle(String title) {
        return title;
    }

    @Override
    public void openEvent(InventoryOpenEvent e) {
        super.openEvent(e);
        manualClose = true;
    }

    @Override
    public void closeEvent(InventoryCloseEvent e) {
        super.closeEvent(e);
        if (manualClose) {
            CLOSE_RUNNABLE.run();
        }
    }

    @Override
    public void topInventoryClickEvent(InventoryClickEvent e, int slot) {
        if (!super.getCooldownManager().tryCooldown(slot)) {
            return;
        }
        char c = super.getLayout().charAt(slot);
        asyncPlaySound("Icons." + c + ".Display.Sound");
        if (c == 'A' || c == 'C') operate(c == 'A');
    }

    /**
     * 点击了某项操作
     * @param isConfirm 是否确定
     */
    private void operate(boolean isConfirm) {
        manualClose = false;
        asyncCloseMenu();
        if (isConfirm) {
            CONFIRM_RUNNABLE.run();
            return;
        }
        CANCEL_RUNNABLE.run();
    }

    /**
     * @author Ryan-0916
     * @Desc 确认菜单构造器
     * @date 2024-07-16
     */
    public static class Builder {

        private ItemStack itemStack;
        private Runnable confirmRunnable;
        private Runnable cancelRunnable;
        private Runnable closeRunnable;
        private Player player;

        public Builder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public Builder confirmTask(Runnable confirmTask) {
            this.confirmRunnable = confirmTask;
            return this;
        }

        public Builder cancelTask(Runnable cancelTask) {
            this.cancelRunnable = cancelTask;
            return this;
        }

        public Builder closeTask(Runnable closeTask) {
            this.closeRunnable = closeTask;
            return this;
        }

        public Builder cancelOrCloseTask(Runnable cancelOrCloseTask) {
            this.cancelRunnable = cancelOrCloseTask;
            this.closeRunnable = cancelOrCloseTask;
            return this;
        }

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public void open() {
            if (player == null
                    || itemStack == null
                    || confirmRunnable == null
                    || cancelRunnable == null
                    || closeRunnable == null) throw new NullPointerException("构建确认菜单时出现未知异常，请填写必填参数");
            new ConfirmMenu(this);
        }
    }
}
