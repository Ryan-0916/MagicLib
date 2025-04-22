package com.magicrealms.magiclib.common.command.records;

import com.magicrealms.magiclib.common.command.enums.CommandSenderType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Command 类表示一个由特定发送者类型发送的命令。
 * 该类封装了命令内容及其发送者类型，用于不同类型的命令执行。
 * @author Ryan-0916
 * @date 2025-04-22
 */
@SuppressWarnings("unused")
public record ExecutableCommand(CommandSenderType senderType, String command) {

    /**
     * 创建一个新的 Command 对象。
     * @param senderType 发送者类型，表示命令来源（如 OP、控制台或玩家）
     * @param command 命令内容
     * @return 一个新的 Command 对象
     */
    public static ExecutableCommand of(CommandSenderType senderType, String command) {
        return new ExecutableCommand(senderType, command);
    }

    /**
     * 创建一个来自 OP（操作员）的命令。
     * @param command 命令内容
     * @return 一个来自 OP 的 Command 对象
     */
    public static ExecutableCommand ofOp(String command) {
        return new ExecutableCommand(CommandSenderType.OP, command);
    }

    /**
     * 创建一个来自控制台的命令。
     * @param command 命令内容
     * @return 一个来自控制台的 Command 对象
     */
    public static ExecutableCommand ofConsole(String command) {
        return new ExecutableCommand(CommandSenderType.CONSOLE, command);
    }

    /**
     * 创建一个来自自身（SELF）发送的命令。
     * @param command 命令内容
     * @return 一个来自自身的 Command 对象
     */
    public static ExecutableCommand ofSelf(String command) {
        return new ExecutableCommand(CommandSenderType.SELF, command);
    }

    /**
     * 执行该指令
     * 根据不同的发送者类型执行不同的指令操作。
     * @param executor 执行该指令的玩家
     *                <p>如果发送者类型是 OP，则通过玩家执行该指令并临时设置玩家为 OP。</p>
     *                <p>如果发送者类型是 CONSOLE，则通过控制台执行该指令。</p>
     *                <p>如果发送者类型是 SELF，则由玩家自己执行该指令。</p>
     */
    public void execute(Player executor) {
        switch (senderType) {
            case CONSOLE ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            case OP -> {
                boolean wasOp = executor.isOp();
                try {
                    if (!wasOp) {
                        executor.setOp(true);
                    }
                    Bukkit.dispatchCommand(executor, command);
                } finally {
                    if (!wasOp) {
                        executor.setOp(false);
                    }
                }
            }
            case SELF ->
                    Bukkit.dispatchCommand(executor, command);
        }
    }
}
