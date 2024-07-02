package com.magicrealms.magiclib.common.message.factory;

import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.enums.MessageType;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ryan-0916
 * @Desc 抽象消息工厂
 * @date 2024-05-06
 **/
public interface IMessageFactory {
    AbstractMessage create(@NotNull MessageType e);
}
