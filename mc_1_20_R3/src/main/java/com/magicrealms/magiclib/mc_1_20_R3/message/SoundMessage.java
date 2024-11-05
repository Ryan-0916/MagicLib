package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.utils.StringUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan-0916
 * @Desc 声音消息
 * @date 2024-05-17
 */
public class SoundMessage extends AbstractMessage {

    private static volatile SoundMessage INSTANCE;

    private final Map<UUID, BukkitTask> TASK;

    private SoundMessage() {
        this.TASK = new HashMap<>();
    }

    public static SoundMessage getInstance() {
        if (INSTANCE == null) {
            synchronized (SoundMessage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SoundMessage();
                }
            }
        }
        
        return INSTANCE;
    }

    /**
     * 给玩家发送一条声音消息
     * @param plugin 要发送消息的插件 {@link MagicRealmsPlugin}
     * @param player 要接收消息的玩家对象
     * @param message 消息内容，内容信息如下
     * 使用方法:
     * <sound>ambient.basalt_deltas.additions</sound> 发送一条声音消息给玩家
     * 其中 ambient.basalt_deltas.additions 为您的消息目录
     * 内连属性:
     * <volume>1.0</volume> 音量，默认值：1.0F
     * <pitch>1.0</pitch> 音准，默认值：1.0F
     * <speed>1</speed> 音速，默认值：1L
     * <times>1</times> 消息的发送次数，默认值：1
     * <interval>1</interval> 消息发送间隔 （秒），默认值：1
     */
    @Override
    public void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull Player player, @NotNull String message) {
        int times = StringUtil.getValueBTWTags(message, "times", 1, ParseType.INTEGER);
        double interval = StringUtil.getValueBTWTags(message, "interval", 1D, ParseType.DOUBLE);
        float volume = StringUtil.getValueBTWTags(message, "volume", 1F, ParseType.FLOAT),
                pitch =  StringUtil.getValueBTWTags(message, "pitch", 1F, ParseType.FLOAT);
        long speed = StringUtil.getValueBTWTags(message, "speed", 1L, ParseType.LONG);
        String path = StringUtil.removeTags(message, "volume", "pitch", "speed").trim();
        if (StringUtils.isBlank(path)) {
            return;
        }
        if (times <= 1) {
            sendSound(player, path, volume, pitch, speed);
            return;
        }
        AtomicInteger index = new AtomicInteger();
        TASK.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || index.get() >= times) {
                cleanMessage(player);
                return;
            }
            sendSound(player, path, volume, pitch, speed);
            index.getAndIncrement();
        }, 0, Math.round(interval * 20)));
    }

    @Override
    public void cleanMessage(@NotNull Player player) {
        Optional.ofNullable(TASK.get(player.getUniqueId())).ifPresent(task -> {
            TASK.remove(player.getUniqueId());
            if (!task.isCancelled()) task.cancel();
        });
    }

    private void sendSound(@NotNull Player player, @NotNull String path, float volume, float pitch, long speed) {
        List<Packet<ClientGamePacketListener>> packetListeners = List.of(
                new ClientboundSoundPacket(Holder.direct(SoundEvent.createVariableRangeEvent(new ResourceLocation(path))),
                        SoundSource.PLAYERS, player.getLocation().getX(),
                        player.getLocation().getY(), player.getLocation().getZ(),
                        volume, pitch, speed)
        );
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundBundlePacket(packetListeners));
    }
}
