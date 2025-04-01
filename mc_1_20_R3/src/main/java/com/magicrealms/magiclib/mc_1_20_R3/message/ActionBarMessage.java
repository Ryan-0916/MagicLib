package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.utils.StringUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
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
     * 给玩家发送一条自定义 ActionBar 消息
     * @param plugin 发送消息的插件  {@link MagicRealmsPlugin}
     * @param player 消息接收者
     * @param message 消息内容，内容信息如下
     * 使用方法：
     * <actionBar>Hello world</actionBar> 发送一条 Hello world 的 ActionBar 消息给玩家
     * 内连属性：
     * <times>1</times> 消息的发送次数，默认值：1
     * <interval>1</interval> 消息发送间隔 （秒），默认值：1
     * <desc>false</desc> 倒序，默认值：false
     * <legacy>false</legacy> 是否使用旧版的MiniMessage格式进行序列化例如颜色 &x&F&F&F&F&F&F 的写法，默认值：false
     * <printer>false</printer> 是否需要打字机一样的效果，每个字单独蹦出，默认值：false
     * 注意项：当 <times> 标签大于 1 时，将不会触发打印机效果
     * <printerTime>1</printerTime> 打印机打印的总耗时，它会自动根据您的字数去决定单个字的耗时时长，默认值：false
     * <printerPrefix>前缀</printerPrefix> 打印的消息的前缀，默认值：空
     * 内置变量:
     * %times% 当前次数，如果 <desc> 属性为 true 则会倒序
     */
    @Override
    public void sendMessage(MagicRealmsPlugin plugin, Player player, String message) {
        cleanMessage(player);
        int times = StringUtil.getValueBTWTags(message, "times", 1, ParseType.INTEGER);
        boolean desc = StringUtil.getValueBTWTags(message, "desc", false, ParseType.BOOLEAN),
                legacy = StringUtil.getValueBTWTags(message, "legacy", false, ParseType.BOOLEAN),
                printer = StringUtil.getValueBTWTags(message, "printer", false, ParseType.BOOLEAN);
        double interval = StringUtil.getValueBTWTags(message, "interval", 1D, ParseType.DOUBLE),
                printerTime = StringUtil.getValueBTWTags(message, "printerTime", 1D, ParseType.DOUBLE);
        String printerPrefix = StringUtil.getStringBTWTags(message, "printerPrefix").orElse(StringUtil.EMPTY),
                msg = StringUtil.removeTags(message, "times", "interval", "desc", "legacy", "printer", "printerTime", "printerPrefix");

        if (times <= 1) {
            String m = StringUtil.replacePlaceholder(msg, "times", "1");
            /* 打印机效果 */
            if (printer) {
                printActionBar(plugin, player, m, printerPrefix, printerTime, legacy);
                return;
            }
            sendActionBar(player, AdventureHelper.serializeComponent(
                    AdventureHelper.deserializeComponent(legacy ? AdventureHelper.legacyToMiniMessage(m) : m)));
            return;
        }

        /* 循环发送消息 */
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
    public void cleanMessage(Player player) {
        Optional.ofNullable(TASK.get(player.getUniqueId())).ifPresent(task -> {
            TASK.remove(player.getUniqueId());
            if (!task.isCancelled()) task.cancel();
        });
    }

    private void sendActionBar(Player player, String msg) {
        ((CraftPlayer)player).getHandle().connection.send(
                new ClientboundSetActionBarTextPacket(Optional.ofNullable(Component.Serializer.fromJson(msg)).orElse(
                        Component.empty())));
    }

    /**
     * 逐字符打印ActionBar消息给玩家，支持标签和前缀。
     * @param player  要接收消息的玩家对象
     * @param msg     要打印的消息内容，包含可能的标签
     * @param prefix  消息的前缀
     * @param time    打印整个消息所需的总时间（单位：秒）
     * @param legacy  是否使用旧版的MiniMessage格式进行序列化
     */
    private void printActionBar(MagicRealmsPlugin plugin, Player player, String msg,
                                String prefix, double time, boolean legacy) {

        /* 将消息中的标签解析为列表
         * 例如 <prefix>前缀</prefix>
         * 将会转换成 prefix::前缀
         * 此处的目的是为了支持 MiniMessage 的标签写法
         * 让截取出来的文本也不会失去 MiniMessage 的色彩或是其他
         */
        List<String> labelMsg = StringUtil.getTagsToList(msg);

        /* 提取去除 MiniMessage 标签后，所显示的真实内容
         * 例如<red>我是红色</red><yellow>我是黄色</yellow>
         * 那么此段内容的真实文本为：我是红色我是黄色，我们应当按照
         * 真实的文字内容计算单个文字的更换时长
         */
        String realMessage = labelMsg.stream().map(e -> {
            String[] parts = e.split("::", 2);
            return parts.length > 1 ? parts[1] : StringUtil.EMPTY;
        }).collect(Collectors.joining());


        /* 当前打印到的字符索引 */
        AtomicInteger index = new AtomicInteger();
        /* 当前构建的消息内容 */
        StringBuilder currentText = new StringBuilder();

        /* 创建一个新的计时器，用于定时更新 ActionBar 消息 */
        TASK.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            /* 如果玩家已经离线或打印完整个消息，则关闭计时器任务 */
            if (!player.isOnline() || index.get() >= realMessage.length()) {
                cleanMessage(player);
                return;
            }
            int i = 0;
            /* 遍历标签列表，构建当前应显示的消息内容
             * 此处是为了不让打印的内容失去 MiniMessage 的色彩或是其他
             * 如果当前输入的内容位于标签后的首位字符，那将会将当前输出内容拥有的标签拼接到构建的消息内容中 */
            for (String label : labelMsg) {
                String[] parts = label.split("::", 2);
                if (i > index.get()) {
                    break;
                } else if (i == index.get() && !parts[0].equals("prefix")) {
                    currentText.append("<").append(parts[0]).append(">");
                }
                i += parts.length >= 2 ? parts[1].length() : 0;
            }
            /* 将内容拼接到构建的消息内容中 */
            currentText.append(realMessage.charAt(index.getAndIncrement()));
            /* 构建完整的消息，包括前缀和当前消息内容，如果是未结束的文本拼接上省略号 */
            String m = prefix + currentText + (currentText.length() == msg.length() ? StringUtil.EMPTY : "...");
            /* 发送ActionBar消息给玩家，根据legacy标志选择适当的序列化方式 */
            sendActionBar(player, AdventureHelper.serializeComponent(
                    AdventureHelper.deserializeComponent(legacy ? AdventureHelper.legacyToMiniMessage(m) : m)));
        }, 0, Math.round(time / (realMessage.length() - 1) * 20)));
    }


}
