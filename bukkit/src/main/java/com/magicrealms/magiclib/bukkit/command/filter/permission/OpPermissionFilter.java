package com.magicrealms.magiclib.bukkit.command.filter.permission;

import com.magicrealms.magiclib.bukkit.command.filter.AbstractChannelFilter;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 权限过滤器 - OP
 * @date 2023-10-01
 */
public class OpPermissionFilter extends AbstractChannelFilter {

    public static final OpPermissionFilter INSTANCE = new OpPermissionFilter();

    private OpPermissionFilter(){}

    @Override
    public Boolean filter(CommandSender sender, String label, String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return sender.isOp();
    }
}
