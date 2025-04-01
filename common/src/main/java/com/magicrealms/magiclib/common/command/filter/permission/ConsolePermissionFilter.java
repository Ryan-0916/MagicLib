package com.magicrealms.magiclib.common.command.filter.permission;

import com.magicrealms.magiclib.common.command.filter.AbstractChannelFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 权限过滤器 - 控制台
 * @date 2023-10-01
 */
public class ConsolePermissionFilter extends AbstractChannelFilter {

    public static final ConsolePermissionFilter INSTANCE = new ConsolePermissionFilter();

    private ConsolePermissionFilter(){}

    @Override
    public Boolean filter(CommandSender sender, String label, String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return sender instanceof ConsoleCommandSender;
    }
}
