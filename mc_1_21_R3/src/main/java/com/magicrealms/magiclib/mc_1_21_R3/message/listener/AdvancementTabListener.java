package com.magicrealms.magiclib.mc_1_21_R3.message.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Ryan-0916
 * @Desc 未来拓展
 * @date 2025-06-04
 */
@SuppressWarnings("unused")
public class AdvancementTabListener extends PacketListenerAbstract {
    private final Player PLAYER;
    private final String COMMAND;
    private final MagicRealmsPlugin PLUGIN;

    public AdvancementTabListener(MagicRealmsPlugin plugin, Player player, String command) {
        super(PacketListenerPriority.MONITOR);
        this.PLUGIN = plugin;
        this.PLAYER = player;
        this.COMMAND = command;
        PacketEvents.getAPI().getEventManager().registerListener(this);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.ADVANCEMENT_TAB) {
            if (PLAYER.getUniqueId().equals(event.getUser().getUUID())) {
                Bukkit.getScheduler().runTask(PLUGIN,() -> {
                            PLAYER.closeInventory();
                            Bukkit.dispatchCommand(PLAYER, COMMAND);
                });
                this.unregister();
            }
        }
    }

    public void unregister() {
        PacketEvents.getAPI().getEventManager().unregisterListener(this);
    }
}
