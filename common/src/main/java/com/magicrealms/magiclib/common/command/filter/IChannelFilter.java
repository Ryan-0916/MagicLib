package com.magicrealms.magiclib.common.command.filter;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 抽象过滤器
 * @date 2023-10-01
 **/
public interface IChannelFilter {
    Boolean filter(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args,
                   @Nullable String text, @Nullable String permission, @Nullable String annotateLabel);
}
