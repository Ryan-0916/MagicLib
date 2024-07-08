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
import java.util.function.Consumer;

import static com.magicrealms.magiclib.paper.MagicLib.YML_CONFIRM_MENU;

/**
 * @author Ryan-0916
 * @Desc 确认菜单
 * @date 2024-07-02
 */
@SuppressWarnings("unused")
public class ConfirmMenu extends BaseMenuHolder {
    private final Consumer<Boolean> callBack;
    private final ItemStack itemStack;
    private boolean manualClose;

    public ConfirmMenu(@NotNull Player player,
                       @NotNull ItemStack itemStack,
                       @NotNull Consumer<Boolean> callBack) {
        super(MagicLib.getInstance(), player, YML_CONFIRM_MENU, "###ABC###");
        this.callBack = callBack;
        this.itemStack = itemStack;
        openMenu();
    }

    private void setMenu() {
        int size =  super.getLayout().length();
        for (int i = 0; i < size; i++){
            if (super.getLayout().charAt(i) == 'B') {
                super.setItemSlot(i, itemStack);
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
            callBack.accept(isConfirm);
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
                callBack.accept(false);
            }
        });
    }

}
