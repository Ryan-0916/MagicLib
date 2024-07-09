package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan-0916
 * @Desc Title 消息
 * @date 2024-05-17
 **/
public class TitleMessage extends AbstractMessage {

    private static volatile TitleMessage INSTANCE;

    private final Map<UUID, BukkitTask> TASK;

    private TitleMessage() {
        this.TASK = new HashMap<>();
    }

    public static TitleMessage getInstance() {
        if (INSTANCE == null) {
            synchronized (TitleMessage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TitleMessage();
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
     * <title>Hello world</title> 发送一条 Hello world 的 Title 消息给玩家
     * 内连属性:
     * <subTitle>Hello</subTitle> 子标题 Hello
     * <in>0.5</in> 消息渐入时间 (秒)
     * <out>0.5</out> 消息渐出时间 (秒)
     * <keep>1</keep> 消息保留时间 (秒)
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
        double interval = StringUtil.getDoubleBTWTags(message, "interval", 1D),
            in = StringUtil.getDoubleBTWTags(message, "in", 0D),
            out = StringUtil.getDoubleBTWTags(message, "out", 0D),
            keep = StringUtil.getDoubleBTWTags(message, "keep", 1D);
        boolean desc = StringUtil.getBooleanBTWTags(message, "desc", false),
                legacy = StringUtil.getBooleanBTWTags(message, "legacy", true);

        String subTitle = StringUtil.getStringBTWTags(message, "subTitle").orElse(StringUtil.EMPTY);
        String title = StringUtil.removeTags(message, "in", "out", "keep", "times", "interval", "desc", "legacy");

        if (times <= 1) {
            sendTitle(player, StringUtil.replacePlaceholder(title, "times", "1"), StringUtil.replacePlaceholder(subTitle, "times", "1"), in, keep, out, legacy);
            return;
        }

        AtomicInteger index = new AtomicInteger();
        TASK.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || index.get() >= times) {
                cleanMessage(player);
                return;
            }
            String time =  String.valueOf(desc ? times - index.get() : index.get() + 1);
            sendTitle(player, StringUtil.replacePlaceholder(title, "times", time),
                    StringUtil.replacePlaceholder(subTitle, "times", time), in, keep, out, legacy);
            index.getAndIncrement();
        }, 0, Math.round(interval * 20)));
    }

    @Override
    public void cleanMessage(@NotNull Player player) {
        Optional.ofNullable(TASK.get(player.getUniqueId())).ifPresent(task -> {
            TASK.remove(player.getUniqueId());
            if (!task.isCancelled()) task.cancel();
        });
        player.clearTitle();
    }

    private void sendTitle(@NotNull Player player,  @NotNull String title, @NotNull String subTitle, double in, double keep, double out, boolean legacy) {
        sendTitle(player,
                AdventureHelper.serializeComponent(
                        AdventureHelper.deserializeComponent(legacy ? AdventureHelper.legacyToMiniMessage(title) : title)),
                AdventureHelper.serializeComponent(
                        AdventureHelper.deserializeComponent( legacy ? AdventureHelper.legacyToMiniMessage(subTitle) : subTitle)),
                (int) Math.round(in * 20),
                (int) Math.round(keep * 20),
                (int) Math.round(out * 20));
    }

    private void sendTitle(@NotNull Player player, @Nullable String title, @Nullable String subTitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        List<Packet<ClientGamePacketListener>> packetListeners = List.of(
                new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks),
                new ClientboundSetTitleTextPacket(Optional.ofNullable(
                        Component.Serializer.fromJson(title == null ? StringUtil.EMPTY : title)).orElse(Component.empty())),
                new ClientboundSetSubtitleTextPacket(Optional.ofNullable(
                        Component.Serializer.fromJson(subTitle == null ? StringUtil.EMPTY : subTitle)).orElse(Component.empty()))
        );
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundBundlePacket(packetListeners));
    }

}
