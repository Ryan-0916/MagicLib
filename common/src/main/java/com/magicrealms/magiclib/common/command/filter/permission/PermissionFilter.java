package com.magicrealms.magiclib.common.command.filter.permission;

import com.magicrealms.magiclib.common.command.filter.AbstractChannelFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 权限过滤器 - 权限节点
 * @date 2023-10-01
 **/
public class PermissionFilter extends AbstractChannelFilter {

    public static final PermissionFilter INSTANCE = new PermissionFilter();

    private PermissionFilter(){}

    @Override
    public Boolean filter(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return sender instanceof Player && (permission == null || sender.hasPermission(permission));
    }
}
