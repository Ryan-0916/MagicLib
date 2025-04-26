package com.magicrealms.magiclib.bukkit.dispatcher;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.store.IRedisStore;
import org.bukkit.command.CommandSender;


/**
 * @author Ryan-0916
 * @Desc 消息调度器接口，用于处理 MagicRealms 插件中的消息发送
 * @date 2024-05-17
 */
@SuppressWarnings("unused")
public interface IMessageDispatcher {

    /**
     * 发送消息至玩家或控制台
     * @param plugin 发送消息的插件实例
     * @param receiver 消息的接收者，可以是玩家或控制台
     * @param msg 需要发送的消息内容
     */
    void sendMessage(MagicRealmsPlugin plugin, CommandSender receiver, String msg);

    /**
     * 发送全服消息
     * @param plugin 发送消息的插件实例
     * @param msg 需要发送的消息内容
     */
    void sendBroadcast(MagicRealmsPlugin plugin, String msg);

    /**
     * 发送跨服消息至指定玩家
     * @param store 消息通道，用于跨服通信
     * @param channel 渠道名称，用于标识消息的分类或目标服务器
     * @param player 接收消息的玩家名称
     * @param msg 需要发送的消息内容
     */
    void sendBungeeMessage(IRedisStore store,
                           String channel, String player, String msg);

    /**
     * 发送跨服全服消息
     * @param store 消息通道，用于跨服通信
     * @param channel 渠道名称，用于标识消息的分类或目标服务器
     * @param msg 要发送的跨服播报消息内容
     */
    void sendBungeeBroadcast(IRedisStore store, String channel, String msg);
}
