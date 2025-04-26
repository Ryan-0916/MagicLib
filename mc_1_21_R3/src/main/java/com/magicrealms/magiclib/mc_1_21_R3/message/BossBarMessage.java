package com.magicrealms.magiclib.mc_1_21_R3.message;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.message.AbstractMessage;
import com.magicrealms.magiclib.common.utils.EnumUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import net.minecraft.world.BossEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan-0916
 * @date 2024-05-17
 */
public class BossBarMessage extends AbstractMessage {

    private static volatile BossBarMessage INSTANCE;

    private BossBarMessage() {}

    public static BossBarMessage getInstance() {
        if (INSTANCE == null) {
            synchronized (BossBarMessage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BossBarMessage();
                }
            }
        }
        
        return INSTANCE;
    }

    /**
     * 给玩家发送一条自定义 BossBar 消息
     * @param plugin 要发送消息的插件 {@link MagicRealmsPlugin}
     * @param player 要接收消息的玩家对象
     * @param message 消息内容，内容信息如下
     * 使用方法：
     * <bossBar>Hello world</bossBar> 发送一条 Hello world 的 BossBar 消息给玩家
     * 内连属性：
     * <times>1</times> 消息的发送次数，默认值：1
     * 该属性决定着您的 BossBar 消息是否过渡颜色与进展，如果该属性为 1 时那他将失去过渡功能
     * <interval>1</interval> 消息发送间隔 （秒），默认值：1
     * <desc>false</desc> 倒序，默认值：false
     * <legacy>false</legacy> 是否使用旧版的MiniMessage格式进行序列化例如颜色 &x&F&F&F&F&F&F 的写法，默认值：false
     * <color>RED</color> BossBar 消息颜色，默认值：RED，可选属性
     * RED-红色，PINK-粉色，BLUE-蓝色，GREEN-绿色，YELLOW-黄色，PURPLE-紫色，WHITE-白色
     * 除此之外我们还允许它的颜色过渡，例如颜色由 红->黄->绿 这由属性 <times> 决定
     * 例如： <times> 设置为 3 次，color 设置为 RED-黄色-绿色 时，它将逐步变化颜色
     * 设置方法：颜色-颜色-颜色 这将代表过渡过程
     * <progress>1.0</progress> BossBar 消息进展
     * 它是 BossBar 的初始进展，取值范围 0-1，当为 1 时初始 BossBar 血量满血，当为 0 时初始 BossBar 不显示血量
     * 默认值：它的默认值由属性 <desc> 决定
     * 当 <desc> 为 true 时 <progress> 默认值：1.0
     * 当 <desc> 为 false 时 <progress> 默认值：0
     * 当 <desc> 为 true 并且 <times> 大于 1 时它将逐步递减
     * 当 <desc> 为 false 并且 <times> 大于 1 时它将逐步递减
     * <overlay>PROGRESS</overlay> BossBar 分段组成部分，默认：PROGRESS，可选属性
     * PROGRESS-不分段，NOTCHED_6-分为6段，NOTCHED_10-分为10段，NOTCHED_12-分为12段，NOTCHED_20-分为20段
     * BossBar 的保持时间由 <times> 与 <interval> 属性决定，保持时间 = times * interval
     * 内置变量:
     * %times% 当前次数，如果 <desc> 属性为 true 则会倒序
     */
    @Override
    public void sendMessage(MagicRealmsPlugin plugin, Player player, String message) {
        cleanMessage(player);
        int times = StringUtil.getValueBetweenTags(message, "times", 1, ParseType.INTEGER);
        double interval = StringUtil.getValueBetweenTags(message, "interval", 1D, ParseType.DOUBLE);
        boolean desc = StringUtil.getValueBetweenTags(message, "desc", false, ParseType.BOOLEAN),
                legacy = StringUtil.getValueBetweenTags(message, "legacy", false, ParseType.BOOLEAN);
        List<BossEvent.BossBarColor> bossBarColors = new ArrayList<>();
        StringUtil.getStringBetweenTags(message, "color")
                .ifPresent(e -> bossBarColors.addAll(EnumUtil.getAllMatchingEnum(BossEvent.BossBarColor.class,
                        e.split("-"))));
        if (bossBarColors.isEmpty()) bossBarColors.add(BossEvent.BossBarColor.RED);
        BossEvent.BossBarOverlay overlay = EnumUtil.getMatchingEnum(BossEvent.BossBarOverlay.class,
                StringUtil.getStringBetweenTags(message, "overlay").orElse("PROGRESS")).orElse(BossEvent.BossBarOverlay.PROGRESS);
        float progress = StringUtil.getValueBetweenTags(message, "progress", desc ? 1.0F : 0F, ParseType.FLOAT);
    }

    @Override
    public void cleanMessage(Player player) {

    }
}
