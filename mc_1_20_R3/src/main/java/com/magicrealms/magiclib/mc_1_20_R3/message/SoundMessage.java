package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.utils.StringUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
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
 **/
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
     * @param plugin 发送消息的插件
     * @param player 消息接收者
     * @param message 消息内容，内容信息如下
     * 使用方法:
     * <sound>ambient.basalt_deltas.additions</sound> 发送一条声音消息给玩家
     * 内连属性:
     * <volume>1</volume> 音量
     * <pitch>1</pitch> 音准默
     * <speed>1</speed> 音速默
     * <times>5</times> 消息发送次数
     * <interval>1.5</interval> 消息发送间隔 （秒）
     */
    @Override
    public void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull Player player, @NotNull String message) {
        int times = StringUtil.getIntegerBTWTags(message, "times", 1);
        double interval = StringUtil.getDoubleBTWTags(message, "interval", 1D);


        float volume = StringUtil.getFloatBTWTags(message, "volume", 1F),
                pitch =  StringUtil.getFloatBTWTags(message, "pitch", 1F);
        long speed = StringUtil.getLongBTWTags(message, "speed", 1L);
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
        EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
        List<Packet<PacketListenerPlayOut>> packetListeners = List.of(
             new PacketPlayOutEntitySound(Holder.a(SoundEffect.a(new MinecraftKey(path))), SoundCategory.h, entityPlayer, volume, pitch, speed)
        );
        entityPlayer.c.b(new ClientboundBundlePacket(packetListeners));
    }
}
