package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 物品栏消息
 * @date 2024-05-17
 */
public class ActionBarMessage extends AbstractMessage {

    private static volatile ActionBarMessage INSTANCE;

    private final Map<UUID, BukkitTask> TASK;

    private ActionBarMessage() {
        this.TASK = new HashMap<>();
    }

    public static ActionBarMessage getInstance() {
        if (INSTANCE == null) {
            synchronized (ActionBarMessage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ActionBarMessage();
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
     * <actionBar>Hello world</actionBar> 发送一条 Hello world 的 Action Bar 消息给玩家
     * 内连属性:
     * <times>5</times> 消息发送次数
     * <interval>1.5</interval> 消息发送间隔 （秒）
     * <desc>true</desc> 倒序 - 默认为 false
     * <legacy>true</legacy> 是否需要支持 &x&F&F&F&F&F&F 的写法
     * <printer>true</printer> 是否需要打字机一样的效果，每个字单独蹦出
     * <printerTime>1</printerTime> 打印的时间
     * <printerPrefix>前缀</printerPrefix> 打印的消息的前缀
     * 内置变量:
     * %times% 当前循环的次数 - 如果反转为 true 则会倒序
     */
    @Override
    public void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull Player player, @NotNull String message) {
        cleanMessage(player);
        int times = StringUtil.getIntegerBTWTags(message, "times", 1);
        boolean desc = StringUtil.getBooleanBTWTags(message, "desc", false),
                legacy = StringUtil.getBooleanBTWTags(message, "legacy", true),
                printer = StringUtil.getBooleanBTWTags(message, "printer", false);
        double interval = StringUtil.getDoubleBTWTags(message, "interval", 1D),
                printerTime = StringUtil.getDoubleBTWTags(message, "printerTime", 1D);

        String printerPrefix = StringUtil.getStringBTWTags(message, "printerPrefix").orElse(StringUtil.EMPTY),
               msg = StringUtil.removeTags(message, "times", "interval", "desc", "legacy", "printer", "printerTime", "printerPrefix");

        if (times <= 1) {
            String m = StringUtil.replacePlaceholder(msg, "times", "1");

            /* 打印机效果 */
            if (printer) {
                printActionBar(player, m, printerPrefix, printerTime, legacy);
                return;
            }

            sendActionBar(player, AdventureHelper.serializeComponent(
                    AdventureHelper.deserializeComponent(legacy ? AdventureHelper.legacyToMiniMessage(m) : m)));
            return;
        }

        AtomicInteger index = new AtomicInteger();
        TASK.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || index.get() >= times) {
                cleanMessage(player);
                return;
            }
            String m = StringUtil.replacePlaceholder(msg, "times", String.valueOf(desc ? times - index.get() : index.get() + 1));
            sendActionBar(player, AdventureHelper.serializeComponent(
                    AdventureHelper.deserializeComponent(legacy ? AdventureHelper.legacyToMiniMessage(m) : m)));
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

    private void sendActionBar(@NotNull Player player, @NotNull String msg) {
        ((CraftPlayer)player).getHandle().connection.send(
                new ClientboundSetActionBarTextPacket(Optional.ofNullable(Component.Serializer.fromJson(msg)).orElse(
                        Component.empty())));
    }

    private void printActionBar(@NotNull Player player, @NotNull String msg,
                                @NotNull String prefix, double time, boolean legacy) {
        List<String> labelMsg = StringUtil.getTagsToList(msg);

        String realMessage = labelMsg.stream().map(e -> {
            String[] parts = e.split("::", 2);
            return parts.length > 1 ? parts[1] : StringUtil.EMPTY;
        }).collect(Collectors.joining());

        new Timer().schedule(new TimerTask() {
            int index = 0;

            final StringBuilder currentText = new StringBuilder();

            @Override
            public void run() {
                if (index >= realMessage.length()) {
                    cancel();
                    return;
                }

                int i = 0;
                for (String label : labelMsg) {
                    String[] parts = label.split("::", 2);
                    if (i > index) {
                        break;
                    } else if (i == index && !parts[0].equals("prefix")) {
                        currentText.append("<").append(parts[0]).append(">");
                    }
                    i += parts.length >= 2 ? parts[1].length() : 0;
                }

                currentText.append(realMessage.charAt(index++));

                String m = prefix + currentText + (currentText.length() == msg.length() ? StringUtil.EMPTY : "...");
                sendActionBar(player, AdventureHelper.serializeComponent(
                        AdventureHelper.deserializeComponent(legacy ? AdventureHelper.legacyToMiniMessage(m) : m)));
            }

            @Override
            public boolean cancel() {
                return super.cancel();
            }

        }, 0, Math.round(time / (realMessage.length() - 1) * 1000L));
    }


}
