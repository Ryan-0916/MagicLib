package com.magicrealms.magiclib.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.dispatcher.MessageDispatcher;
import org.bukkit.command.CommandSender;

import java.util.Locale;

import static com.magicrealms.magiclib.core.MagicLibConstant.YML_LANGUAGE;

/**
 * @author Ryan-0916
 * @Desc 核心部分命令
 * @date 2025-05-05
 */
@CommandListener
@SuppressWarnings("unused")
public class CoreController {

    @Command(text = "^Reload$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magiclib.all||magic.command.magiclib.reload")
    public void reload(CommandSender sender, String[] args){
        MagicLib.getInstance().getConfigManager().reloadAllConfig();
        /* 重置 Offset 部分 */
        MagicLib.getInstance().setupOffset();
        MessageDispatcher.getInstance()
                .sendMessage(MagicLib.getInstance(), sender,
                        MagicLib.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\sAll$",
            permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magiclib.all||magic.command.magiclib.reload")
    public void reloadAll(CommandSender sender, String[] args){
        /* 重置 Offset 部分 */
        MagicLib.getInstance().setupOffset();
        MessageDispatcher.getInstance()
                .sendMessage(MagicLib.getInstance(), sender,
                        MagicLib.getInstance().getConfigManager()
                                .getYmlValue(YML_LANGUAGE,
                                        "PlayerMessage.Success.ReloadFile"));
    }

    @Command(text = "^Reload\\s(?!all\\b)\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload")
    public void reloadBy(CommandSender sender, String[] args){
        MagicLib.getInstance().getConfigManager()
                .reloadConfig(args[1], e -> {
            if (!e) {
                MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender,
                        MagicLib.getInstance().getConfigManager().getYmlValue(YML_LANGUAGE,
                                "PlayerMessage.Error.ReloadFile"));
                return;
            }
            if (args[1].toLowerCase(Locale.ROOT).equals("offset")) {
                MagicLib.getInstance().setupOffset();
            }
            MessageDispatcher.getInstance().sendMessage(MagicLib.getInstance(), sender,
                    MagicLib.getInstance().getConfigManager()
                            .getYmlValue(YML_LANGUAGE,
                            "PlayerMessage.Success.ReloadFile"));
        });
    }
}
