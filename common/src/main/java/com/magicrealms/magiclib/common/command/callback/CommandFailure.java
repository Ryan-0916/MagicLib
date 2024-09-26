package com.magicrealms.magiclib.common.command.callback;

import com.magicrealms.magiclib.common.command.enums.CommandFailureCause;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ryan-0916
 * @Desc 指令执行失败信息
 * @date 2024-05-10
 */
public record CommandFailure(@NotNull CommandSender sender, @NotNull CommandFailureCause cause) {}
