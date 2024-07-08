package com.magicrealms.magiclib.mc_1_20_R1.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan-0916
 * @Desc 消息栏消息
 * @date 2024-05-17
 **/
public class Message extends AbstractMessage {

    private static volatile Message INSTANCE;

    private final Map<UUID, BukkitTask> TASK;

    private Message() {
        this.TASK = new HashMap<>();
    }

    public static Message getInstance() {
        if (INSTANCE == null) {
            synchronized (Message.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Message();
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
     * <message>Hello world</message> 发送一条 Hello world 的消息给玩家
     * 内连属性:
     * <times>5</times> 消息发送次数
     * <interval>1.5</interval> 消息发送间隔 （秒）
     * <desc>true</desc> 倒序 - 默认为 false
     * <legacy>true</legacy> 是否需要支持 &x&F&F&F&F&F&F 的写法
     * 内置变量:
     * %times% 当前循环的次数 - 如果反转为 true 则会倒序
     */
    @Override
    public void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull Player player, @NotNull String message) {
        cleanMessage(player);
        int times = StringUtil.getIntegerBTWTags(message, "times", 1);
        double interval = StringUtil.getDoubleBTWTags(message, "interval", 1D);
        boolean desc = StringUtil.getBooleanBTWTags(message, "desc", false),
                legacy = StringUtil.getBooleanBTWTags(message, "legacy", true);
        String msg = StringUtil.removeTags(message, "times", "interval", "desc", "legacy");
        if (times <= 1) {
            String m = StringUtil.replacePlaceholder(msg, "times", "1");
            player.sendMessage(AdventureHelper.deserializeComponent(
                            legacy ? AdventureHelper.legacyToMiniMessage(m) : m));
            return;
        }

        AtomicInteger index = new AtomicInteger();
        TASK.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || index.get() >= times) {
                cleanMessage(player);
                return;
            }
            String m = StringUtil.replacePlaceholder(msg, "times", String.valueOf(desc ? times - index.get() : index.get() + 1));
            player.sendMessage(AdventureHelper.deserializeComponent(
                    legacy ? AdventureHelper.legacyToMiniMessage(m) : m));
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
}
