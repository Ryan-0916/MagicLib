package com.magicrealms.magiclib.common.command.filter.permission;

import com.magicrealms.magiclib.common.command.filter.AbstractChannelFilter;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 权限过滤器 - 所有人
 * @date 2023-10-01
 */
public class AllPermissionFilter extends AbstractChannelFilter {

    public static final AllPermissionFilter INSTANCE = new AllPermissionFilter();

    private AllPermissionFilter(){}

    @Override
    public Boolean filter(CommandSender sender, String label, String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return true;
    }
}
