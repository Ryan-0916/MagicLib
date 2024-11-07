package com.magicrealms.magiclib.common.command.records;

import com.magicrealms.magiclib.common.command.enums.CommandFailureCause;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ryan-0916
 * @Desc 指令执行失败记录
 * @date 2024-05-10
 */
public record CommandFailure(@NotNull CommandSender sender, @NotNull CommandFailureCause cause) {}
