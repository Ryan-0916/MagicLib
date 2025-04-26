package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.AbstractMessage;
import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.mc_1_20_R3.utils.ComponentUtil;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan-0916
 * @date 2024-05-17
 */
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
     * 给玩家发送一条消息
     * @param plugin 要发送消息的插件 {@link MagicRealmsPlugin}
     * @param player 要接收消息的玩家对象
     * @param message 消息内容，内容信息如下
     * 使用方法:
     * <message>Hello world</message> 发送一条 Hello world 的消息给玩家
     * 内连属性：
     * <times>1</times> 消息的发送次数，默认值：1
     * <interval>1</interval> 消息发送间隔 （秒），默认值：1
     * <desc>false</desc> 倒序，默认值：false
     * <legacy>false</legacy> 是否使用旧版的MiniMessage格式进行序列化例如颜色 &x&F&F&F&F&F&F 的写法，默认值：false
     * 内置变量:
     * %times% 当前次数，如果 <desc> 属性为 true 则会倒序
     */
    @Override
    public void sendMessage(MagicRealmsPlugin plugin, Player player, String message) {
        cleanMessage(player);
        int times = StringUtil.getValueBetweenTags(message, "times", 1, ParseType.INTEGER);
        boolean desc = StringUtil.getValueBetweenTags(message, "desc", false, ParseType.BOOLEAN),
                legacy = StringUtil.getValueBetweenTags(message, "legacy", false, ParseType.BOOLEAN);
        double interval = StringUtil.getValueBetweenTags(message, "interval", 1D, ParseType.DOUBLE);

        String msg = StringUtil.removeTags(message, "times", "interval", "desc", "legacy");
        if (times <= 1) {
            String m = StringUtil.replacePlaceholder(msg, "times", "1");
            sendMessage(player, AdventureHelper.serializeComponent(AdventureHelper.deserializeComponent(
                    legacy ? AdventureHelper.legacyToMiniMessage(m) : m)));
            return;
        }
        AtomicInteger index = new AtomicInteger();
        TASK.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || index.get() >= times) {
                cleanMessage(player);
                return;
            }
            String m = StringUtil.replacePlaceholder(msg, "times", String.valueOf(desc ? times - index.get() : index.get() + 1));
            sendMessage(player, AdventureHelper.serializeComponent(AdventureHelper.deserializeComponent(
                    legacy ? AdventureHelper.legacyToMiniMessage(m) : m)));
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

    private void sendMessage(Player player, String msg) {
        ((CraftPlayer) player).getHandle().connection.send(
                new ClientboundSystemChatPacket(
                        ComponentUtil.getComponentOrEmpty(msg),false));
    }
}
