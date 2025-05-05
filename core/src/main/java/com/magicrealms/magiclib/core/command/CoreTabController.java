package com.magicrealms.magiclib.core.command;

import com.magicrealms.magiclib.bukkit.command.annotations.CommandListener;
import com.magicrealms.magiclib.bukkit.command.annotations.TabComplete;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ryan-0916
 * @Desc 核心部分命令补全
 * @date 2025-05-05
 */
@CommandListener
@SuppressWarnings("unused")
public class CoreTabController {

    private static final Supplier<Stream<String>> fileNames
            = () -> Stream.of("all", "language", "offset", "confirm");

    @TabComplete(text = "^\\s?$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload")
    public List<String> first(CommandSender sender, String[] args) {
        return Stream.of("reload")
                .collect(Collectors.toList());
    }

    @TabComplete(text = "^\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload")
    public List<String> firstTab(CommandSender sender, String[] args) {
        return Stream.of("reload")
                .filter(e ->
                        StringUtils.startsWithIgnoreCase(e, args[0]))
                .collect(Collectors.toList());
    }

    @TabComplete(text = "^Reload\\s$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload")
    public List<String> reload(CommandSender sender, String[] args) {
        return fileNames.get().collect(Collectors.toList());
    }

    @TabComplete(text = "^Reload\\s\\S+$", permissionType = PermissionType.CONSOLE_OR_PERMISSION,
            permission = "magic.command.magicchat.all||magic.command.magicchat.reload")
    public List<String> reloadTab(CommandSender sender, String[] args) {
        return fileNames.get().filter(e ->
                StringUtils.startsWithIgnoreCase(e, args[1])).collect(Collectors.toList());
    }
}
