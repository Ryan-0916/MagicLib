package com.magicrealms.magiclib.mc_1_21_R3.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.mc_1_21_R3.utils.ComponentUtil;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan-0916
 * @date 2024-05-17
 */
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
     * 给玩家发送一条 Title 消息
     * @param plugin 要发送消息的插件 {@link MagicRealmsPlugin}
     * @param player 要接收消息的玩家对象
     * @param message 消息内容，内容信息如下
     * 使用方法:
     * <title>Hello world</title> 发送一条 Hello world 的 Title 消息给玩家
     * 内连属性:
     * <times>1</times> 消息的发送次数，默认值：1
     * <interval>1</interval> 消息发送间隔 （秒），默认值：1
     * <desc>false</desc> 倒序，默认值：false
     * <legacy>false</legacy> 是否使用旧版的MiniMessage格式进行序列化例如颜色 &x&F&F&F&F&F&F 的写法，默认值：false
     * <subTitle>Hello</subTitle> 子标题，默认值：空
     * <in>0</in> 消息渐入时间 (秒)，默认值：0D
     * <out>0</out> 消息渐出时间 (秒)，默认值：0D
     * <keep>0</keep> 消息保留时间 (秒)，默认值：0D
     * 内置变量:
     * %times% 当前次数，如果 <desc> 属性为 true 则会倒序
     */
    @Override
    public void sendMessage(MagicRealmsPlugin plugin, Player player, String message) {
        cleanMessage(player);
        int times = StringUtil.getValueBTWTags(message, "times", 1, ParseType.INTEGER);
        double interval = StringUtil.getValueBTWTags(message, "interval", 1D, ParseType.DOUBLE),
                in = StringUtil.getValueBTWTags(message, "in", 0D, ParseType.DOUBLE),
                out = StringUtil.getValueBTWTags(message, "out", 0D, ParseType.DOUBLE),
                keep = StringUtil.getValueBTWTags(message, "keep", 1D, ParseType.DOUBLE);
        boolean desc = StringUtil.getValueBTWTags(message, "desc", false, ParseType.BOOLEAN),
                legacy = StringUtil.getValueBTWTags(message, "legacy", false, ParseType.BOOLEAN);
        String subTitle = StringUtil.getStringBTWTags(message, "subTitle").orElse(StringUtil.EMPTY);
        String title = StringUtil.removeTags(message, "in", "out", "keep", "times", "interval", "desc", "legacy", "subTitle");
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
    public void cleanMessage(Player player) {
        Optional.ofNullable(TASK.get(player.getUniqueId())).ifPresent(task -> {
            TASK.remove(player.getUniqueId());
            if (!task.isCancelled()) task.cancel();
        });
        player.clearTitle();
    }

    private void sendTitle(Player player,  String title, String subTitle, double in, double keep, double out, boolean legacy) {
        sendTitle(player,
                ComponentUtil.serializeComponent(title, legacy),
                ComponentUtil.serializeComponent(subTitle, legacy),
                (int) Math.round(in * 20),
                (int) Math.round(keep * 20),
                (int) Math.round(out * 20));
    }

    private void sendTitle(Player player, @Nullable String title, @Nullable String subTitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        List<Packet<? super ClientGamePacketListener>> packets = List.of(
                new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks),
                new ClientboundSetTitleTextPacket(ComponentUtil.getComponentOrEmpty(title)),
                new ClientboundSetSubtitleTextPacket(ComponentUtil.getComponentOrEmpty(subTitle))
        );
        ((CraftPlayer)player).getHandle().connection.send(new ClientboundBundlePacket(packets));
    }

}
