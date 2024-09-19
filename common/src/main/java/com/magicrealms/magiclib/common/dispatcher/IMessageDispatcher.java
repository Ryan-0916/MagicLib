package com.magicrealms.magiclib.common.dispatcher;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.store.IRedisStore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ryan-0916
 * @Desc 消息调度器
 * @date 2024-05-17
 */
@SuppressWarnings("unused")
public interface IMessageDispatcher {

    /**
     * 发送消息至玩家或控制台
     * @param plugin 发送的插件
     * @param receiver 接收者
     * @param msg 消息内容
     */
    void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull CommandSender receiver, @NotNull String msg);

    /**
     * 发送全服播报
     * @param plugin 发送的插件
     * @param msg 消息内容
     */
    void sendBroadcast(@NotNull MagicRealmsPlugin plugin, @NotNull String msg);

    /**
     * 发送跨服消息至玩家
     * @param store 消息通道
     * @param channel 渠道名称
     * @param player 玩家名称
     * @param msg 消息内容
     */
    void sendBungeeMessage(@NotNull IRedisStore store,
                           @NotNull String channel, @NotNull String player, @NotNull String msg);

    /**
     * 发送跨服播报
     * @param store 消息通道
     * @param channel 渠道名称
     * @param msg 消息内容
     */
    void sendBungeeBroadcast(@NotNull IRedisStore store, @NotNull String channel, @NotNull String msg);
}
