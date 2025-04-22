package com.magicrealms.magiclib.common.packet.annotations;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ryan-0916
 * @Desc 发送数据包注解
 * 声明方法可接收的 Minecraft 数据包，必须在类注解 @PacketListener 中才生效。
 * 可用于监听指定的协议、发送方和数据包 ID。
 * 注解用于标记一个方法是用于接收特定的 Minecraft 数据包。
 * 该注解必须在类注解 @PacketListener 中才能生效。它用于指示该方法监听特定协议、发送方和数据包 ID。
 * 该注解主要用于与 PacketEvents 配合使用，允许开发者通过方法来处理来自 Minecraft 游戏客户端或服务器的不同类型的数据包。
 * 使用此注解时，可以指定数据包的协议类型、发送方（客户端或服务器）以及数据包的枚举名称。同时，还可以设置该方法的监听器优先级，
 * 以便在多个监听器处理相同数据包时，控制执行顺序。
 * @date 2025-04-10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SuppressWarnings("unused")
public @interface Send {

    /**
     * 指定监听的数据包协议。
     * 包括客户端与服务器之间的协议数据包类型。
     * @return 数据包协议类型
     */
    ConnectionState state();

    /**
     * 指定数据包的发送方。
     * 可以是客户端（CLIENT）或服务器（SERVER）。
     * @return 数据包发送方
     */
    PacketSide side();

    /**
     * 指定数据包枚举 Name
     * 每个数据包都有唯一的 Name 用于标识该数据包类型。
     * @return 数据包枚举 Name
     */
    String name();

    /**
     * 监听器优先级
     * 该字段用于设置该方法的监听器优先级。
     * 优先级决定了在多个方法接收相同数据包时，哪个方法先处理数据包。
     * 默认为 NORMAL，表示普通优先级。
     * @return 监听器优先级，默认值为 PacketListenerPriority.NORMAL
     */
    PacketListenerPriority priority() default PacketListenerPriority.NORMAL;

}
