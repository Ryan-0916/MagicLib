package com.magicrealms.magiclib.paper.listener;

import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.ConnectionState;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.packet.annotations.PacketListener;
import com.magicrealms.magiclib.common.packet.annotations.Send;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ryan-0916
 * @Desc Summon 指令包监听
 * @date 2025-04-11
 */
@PacketListener
@SuppressWarnings("unused")
public class SummonListener {

    /**
     * 处理服务器发送到客户端数据包时的逻辑
     * 数据包协议 PLAY {@link ConnectionState#PLAY}
     * 数据包发送者 SERVER {PacketSide#SERVER}
     * 数据包名称 SYSTEM_CHAT_MESSAGE
     * 对应数据包 {@link PacketType.Play.Server#SYSTEM_CHAT_MESSAGE}
     * 拦截自定义生物 magiclib.custom 生成时给客户端发的系统消息数据包
     */
    @Send(state = ConnectionState.PLAY,
            side = PacketSide.SERVER,
            name = "SYSTEM_CHAT_MESSAGE",
            priority = PacketListenerPriority.LOWEST)
    public void onSpawnEntitySystemChat(PacketSendEvent event) {
        /* summon 生成生物时拦截自定义生物 */
        WrapperPlayServerSystemChatMessage msg
                = new WrapperPlayServerSystemChatMessage(event);
        if (StringUtils.contains(AdventureHelper.serializeComponent(msg.getMessage()),
                "magiclib.custom")) {
            event.setCancelled(true);
        }
    }

    /**
     * 处理服务器发送到客户端数据包时的逻辑
     * 数据包协议 PLAY {@link ConnectionState#PLAY}
     * 数据包发送者 SERVER {PacketSide#SERVER}
     * 数据包名称 SPAWN_ENTITY
     * 对应数据包 {@link PacketType.Play.Server#SPAWN_ENTITY}
     * 拦截自定义生物 magiclib.custom 生成时给客户端发的生物生成数据包
     */
    @Send(state = ConnectionState.PLAY,
            side = PacketSide.SERVER,
            name = "SPAWN_ENTITY",
            priority = PacketListenerPriority.LOWEST)
    public void onSpawnEntity(PacketSendEvent event) {
        /* summon 生成生物时拦截自定义生物 */
        WrapperPlayServerSpawnEntity entity = new WrapperPlayServerSpawnEntity(event);
        if (entity.getEntityType() == null) {
            event.setCancelled(true);
        }
    }

}
