package com.magicrealms.magiclib.bukkit.command.filter.permission;

import com.magicrealms.magiclib.bukkit.command.filter.AbstractChannelFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

/**
 * @author Ryan-0916
 * @Desc 权限过滤器 - 玩家
 * @date 2023-10-01
 */
public class PlayerPermissionFilter extends AbstractChannelFilter {

    public static final PlayerPermissionFilter INSTANCE = new PlayerPermissionFilter();

    private PlayerPermissionFilter(){}

    @Override
    public Boolean filter(CommandSender sender, String label, String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return sender instanceof Player;
    }
}
