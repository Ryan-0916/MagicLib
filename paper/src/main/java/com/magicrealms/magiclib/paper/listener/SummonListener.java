package com.magicrealms.magiclib.paper.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
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

    @Send(protocol = PacketType.Protocol.PLAY,
            sender = PacketType.Sender.SERVER,
            packetId = 108,
            priority = ListenerPriority.HIGHEST)
    public void onSpawnEntitySystemChat(PacketEvent event) {
        /* summon 生成生物时拦截自定义生物 */
        if (StringUtils.contains(event.getPacket().getChatComponents().read(0).getJson(),"magiclib.custom")) {
            event.setCancelled(true);
        }
    }

    @Send(protocol = PacketType.Protocol.PLAY,
            sender = PacketType.Sender.SERVER,
            packetId = 1,
            priority = ListenerPriority.HIGHEST)
    public void onSpawnEntity(PacketEvent event) {
        /* summon 生成生物时拦截自定义生物 */
        if (event.getPacket().getEntityTypeModifier().read(0) == null) {
            event.setCancelled(true);
        }
    }

}
