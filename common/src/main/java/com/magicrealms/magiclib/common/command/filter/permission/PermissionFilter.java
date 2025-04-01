package com.magicrealms.magiclib.common.command.filter.permission;

import com.magicrealms.magiclib.common.command.filter.AbstractChannelFilter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author Ryan-0916
 * @Desc 权限过滤器 - 权限节点
 * @date 2023-10-01
 */
public class PermissionFilter extends AbstractChannelFilter {

    public static final PermissionFilter INSTANCE = new PermissionFilter();

    private PermissionFilter(){}

    @Override
    public Boolean filter(CommandSender sender, String label, String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {

        String[] permissions = permission == null ? null : StringUtils.split(permission, "||");
        return sender instanceof Player && (permissions == null || Arrays.stream(permissions).anyMatch(sender::hasPermission));
    }
}
