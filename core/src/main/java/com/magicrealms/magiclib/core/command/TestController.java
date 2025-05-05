package com.magicrealms.magiclib.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.core.menu.ConfirmMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-05-05
 */
@CommandListener
@SuppressWarnings("unused")
public class TestController {

    @Command(text = "^Test$", permissionType = PermissionType.PLAYER)
    public void test(Player sender, String[] args) {
        new ConfirmMenu.Builder()
                .confirmTask(() -> MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender, "你点击了确认"))
                .closeTask(() -> MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender, "点击了关闭"))
                .cancelTask(() -> MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender, "你取消了"))
                .itemStack(new ItemStack(Material.STONE))
                .player(sender)
                .open();
    }


}
