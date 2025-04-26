package com.magicrealms.magiclib.bukkit.command.enums;

/**
 * @author Ryan-0916
 * @Desc 指令权限匹配
 * @date 2023-10-01
 */
public enum PermissionType {
    /* 所有角色 */
    ALL,
    /* 玩家 （包含管理者）*/
    PLAYER,
    /* 拥有某项权限组 */
    PERMISSION,
    /* 仅管理员 */
    ADMIN,
    /* 仅控制台 */
    CONSOLE,
    /* 管理员或控制台 */
    OP,
    /* 控制台或者拥有某项权限组 */
    CONSOLE_OR_PERMISSION
}