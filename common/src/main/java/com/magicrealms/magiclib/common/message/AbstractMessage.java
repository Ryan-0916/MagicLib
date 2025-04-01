package com.magicrealms.magiclib.common.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import org.bukkit.entity.Player;


/**
 * @author Ryan-0916
 * @Desc 抽象消息类
 * @date 2024-05-06
 */
public abstract class AbstractMessage {

    /**
     * 发送消息
     * @param player 消息接收者
     * @param message 消息
     */
    public abstract void sendMessage(MagicRealmsPlugin plugin, Player player, String message);

    /**
     * 清空等待发送的消息
     * @param player 玩家
     */
    public abstract void cleanMessage(Player player);
}
