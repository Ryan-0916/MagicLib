package com.magicrealms.magiclib.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.MagicLib;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-05-05
 */
@CommandListener
@SuppressWarnings("unused")
public class TestController {

    @Command(text = "^Test\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void test(Player sender, String[] args) {
        System.out.println(MagicLib.getInstance().getAdvanceManager().getAdvance(args[1]));
    }


}
