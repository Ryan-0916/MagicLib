package com.magicrealms.magiclib.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.core.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-05-05
 */
@CommandListener
@SuppressWarnings("unused")
public class TestController {

//    @Command(text = "^Test\\s\\S+$", permissionType = PermissionType.PLAYER)
//    public void test(Player sender, String[] args) {
//        System.out.println(MagicLib.getInstance().getAdvanceManager().getAdvance(args[1]));
//    }

    @Command(text = "^Test\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void test2(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) { return; }
        long time = System.currentTimeMillis();
        List<ItemStack> itemStacks = new ArrayList<>(Arrays.asList(target.getInventory()
                .getStorageContents()));
        MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender,
                "是否可以将玩家" +args[1]+ "背包中的物品，全部堆叠至我的背包：" +
                        ItemUtil.canFitIntoInventory(sender, itemStacks) +
                        "总耗时：" + (System.currentTimeMillis() - time));
    }

}
