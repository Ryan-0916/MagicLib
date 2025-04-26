package com.magicrealms.magiclib.bukkit.command.filter.permission;

import com.magicrealms.magiclib.bukkit.command.filter.AbstractChannelFilter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * @author Ryan-0916
 * @Desc 控制台或者权限组
 * @date 2024-07-10
 */
public class ConsoleOrPermissionFilter extends AbstractChannelFilter {

    public static final ConsoleOrPermissionFilter INSTANCE = new ConsoleOrPermissionFilter();

    private ConsoleOrPermissionFilter(){}

    @Override
    public Boolean filter(CommandSender sender, String label, String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        String[] permissions = permission == null ? null : StringUtils.split(permission, "||");
        return sender instanceof ConsoleCommandSender || (sender instanceof Player
                && (permissions == null || Arrays.stream(permissions).anyMatch(sender::hasPermission)));
    }
}