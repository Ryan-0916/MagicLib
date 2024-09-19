package com.magicrealms.magiclib.common.command.enums;

/**
 * @author Ryan-0916
 * @Desc 指令失败原因
 * @date 2023-10-01
 */
public enum CommandFailureCause {
    /* 执行者不是玩家 */
    NOT_PLAYER,
    /* 执行者不是控制台 */
    NOT_CONSOLE,
    /* 执行者权限不足时 */
    PERMISSION_DENIED,
    /* 未知指令时 */
    UN_KNOWN_COMMAND,
}
