package com.magicrealms.magiclib.common.command.filter.permission;

import com.magicrealms.magiclib.common.command.filter.AbstractChannelFilter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
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
    public Boolean filter(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return sender.isOp();
    }
}
