package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.AbstractMessage;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.mc_1_20_R3.dispatcher.MC_1_20_R3_NMSDispatcher;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


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
    public void sendMessage(MagicRealmsPlugin plugin, Player player, String message) {
        int times = StringUtil.getValueBetweenTags(message, "times", 1, ParseType.INTEGER);
        double interval = StringUtil.getValueBetweenTags(message, "interval", 1D, ParseType.DOUBLE);
        float volume = StringUtil.getValueBetweenTags(message, "volume", 1F, ParseType.FLOAT),
                pitch =  StringUtil.getValueBetweenTags(message, "pitch", 1F, ParseType.FLOAT);
        long speed = StringUtil.getValueBetweenTags(message, "speed", 1L, ParseType.LONG);
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
    public void cleanMessage(Player player) {
        Optional.ofNullable(TASK.get(player.getUniqueId())).ifPresent(task -> {
            TASK.remove(player.getUniqueId());
            if (!task.isCancelled()) task.cancel();
        });
    }

    private void sendSound(Player player, String namespace, float volume, float pitch, long speed) {
        MC_1_20_R3_NMSDispatcher.getInstance().playSound(player, namespace, volume, pitch, speed);
    }
}
