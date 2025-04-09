package com.magicrealms.magiclib.paper.dispatcher;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.dispatcher.IMessageDispatcher;
import com.magicrealms.magiclib.common.exception.UnsupportedVersionException;
import com.magicrealms.magiclib.common.message.bungee.BungeeMessage;
import com.magicrealms.magiclib.common.message.enums.MessageType;
import com.magicrealms.magiclib.common.message.factory.IMessageFactory;
import com.magicrealms.magiclib.common.message.helper.AdventureHelper;
import com.magicrealms.magiclib.common.store.IRedisStore;
import com.magicrealms.magiclib.common.utils.JsonUtil;
import com.magicrealms.magiclib.common.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ryan-0916
 * @Desc 消息调度器
 * @date 2024-05-17
 */
@SuppressWarnings("unused")
public class MessageDispatcher implements IMessageDispatcher {
    private static volatile MessageDispatcher INSTANCE;
    private final IMessageFactory MESSAGE_FACTORY;

    private MessageDispatcher() {
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        String packageName;
        switch (bukkitVersion) {
            case "1.21.4" -> packageName = "mc_1_21_R3";
            case "1.20.3", "1.20.4" -> packageName = "mc_1_20_R3";
            case "1.20", "1.20.1" -> packageName = "mc_1_20_R1";
            default -> throw new UnsupportedVersionException("您的 Minecraft 版本不兼容，请使用合适的版本");
        }
        try {
            Class<?> clazz = Class.forName("com.magicrealms.magiclib." + packageName + ".message.factory." + StringUtils.upperCase(packageName) + "_MessageFactory");
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            MESSAGE_FACTORY = (IMessageFactory) constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("MagicLib 初始化失败", e);
        }
    }

    public static MessageDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized(MessageDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MessageDispatcher();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 给对象发送一条消息
     * @param plugin 发送消息的插件
     * @param receiver 接收者
     * @param message 消息内容
     */
    @Override
    public void sendMessage(MagicRealmsPlugin plugin,
                            CommandSender receiver,
                            String message) {
        if (receiver instanceof Player player) {
            Map<String, String> map = new HashMap<>();
            map.put("player_name", player.getName());
            String msg = StringUtil.replacePlaceholder(message, map);
            AtomicBoolean sent = new AtomicBoolean(false);
            Arrays.stream(MessageType.values()).forEach(type -> StringUtil.getStringBTWTags(msg, type.getTag()).ifPresent(m -> {
                sent.set(true);
                MESSAGE_FACTORY.create(type).sendMessage(plugin, player, m);
            }));
            if (!sent.get()) {
                MESSAGE_FACTORY.create(MessageType.MESSAGE).sendMessage(plugin, player, message);
            }
            return;
        }
        receiver.sendMessage(AdventureHelper.deserializeComponent(AdventureHelper.legacyToMiniMessage(message)));
    }

    @Override
    public void sendBroadcast(MagicRealmsPlugin plugin, String msg) {
        Bukkit.getOnlinePlayers().forEach(e -> {
            if (e == null || e.getPlayer() == null) return;
            sendMessage(plugin, e.getPlayer(), msg);
        });
    }

    @Override
    public void sendBungeeMessage(IRedisStore store,
                                  String channel, String player, String msg) {
        BungeeMessage message = new BungeeMessage(player, msg);
        store.publishValue(channel, JsonUtil.objectToJson(message));
    }

    @Override
    public void sendBungeeBroadcast(IRedisStore store, String channel, String msg) {
        BungeeMessage message = new BungeeMessage(msg);
        store.publishValue(channel, JsonUtil.objectToJson(message));
    }


}
