package com.magicrealms.magiclib.core.controller;
import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import com.magicrealms.magiclib.core.entity.InputValidatorResult;
import com.magicrealms.magiclib.core.menu.InputMenu;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2024-07-16
 */
@CommandListener
@SuppressWarnings("unused")
public class TestController {

    @Command(text = "^test\\s\\S+$", permissionType = PermissionType.PLAYER)
    public void test2(CommandSender sender, String[] args) {
        MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender,
                args[1]);
    }

    @Command(text = "^test$", permissionType = PermissionType.PLAYER)
    public void test(CommandSender sender, String[] args) {
        new InputMenu.Builder().plugin(MagicLib.getInstance())
                .player((Player) sender)
                .customValidator(v -> {
                    InputValidatorResult result = new InputValidatorResult();
                    if (StringUtils.isBlank(v)) {
                        result.setMessage("您为啥输入空啊");
                        return result;
                    }
                    if (v.equals("大家好")) {
                        result.setMessage("你是蔡徐坤？");
                        return result;
                    }
                    result.setMessage("你输入的文字符合");
                    result.setValidator(true);
                    return result;
                })
                .resultSlot(1)
                .itemStack(new ItemStack(Material.STONE))
                .cancelTask(() -> {})
                .confirmConsumer(e -> MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(),
                        sender, e))
                .open();
    }

}
