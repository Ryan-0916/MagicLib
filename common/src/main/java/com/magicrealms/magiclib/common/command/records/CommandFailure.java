package com.magicrealms.magiclib.common.command.records;

import com.magicrealms.magiclib.common.command.enums.CommandFailureCause;
import org.bukkit.command.CommandSender;


/**
 * @author Ryan-0916
 * @Desc 指令执行失败记录
 * @date 2024-05-10
 */
public record CommandFailure(CommandSender sender, CommandFailureCause cause) {

    public static CommandFailure of(CommandSender sender, CommandFailureCause cause) {
        return new CommandFailure(sender, cause);
    }

}
