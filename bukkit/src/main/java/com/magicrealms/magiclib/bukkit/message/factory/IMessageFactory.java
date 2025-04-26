package com.magicrealms.magiclib.bukkit.message.factory;

import com.magicrealms.magiclib.bukkit.message.AbstractMessage;
import com.magicrealms.magiclib.bukkit.message.enums.MessageType;


/**
 * @author Ryan-0916
 * @Desc 抽象消息工厂
 * @date 2024-05-06
 */
public interface IMessageFactory {
    AbstractMessage create(MessageType e);
}
