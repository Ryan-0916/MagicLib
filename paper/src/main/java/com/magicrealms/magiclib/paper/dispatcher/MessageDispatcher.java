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
import com.magicrealms.magiclib.mc_1_20_R1.message.factory.MC_1_20_R1_MessageFactory;
import com.magicrealms.magiclib.mc_1_20_R3.message.factory.MC_1_20_R3_MessageFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2024-05-17
 **/
@SuppressWarnings("unused")
public class MessageDispatcher implements IMessageDispatcher {
    private static volatile MessageDispatcher INSTANCE;
    private final IMessageFactory MESSAGE_FACTORY;

    private MessageDispatcher() {
        MESSAGE_FACTORY = switch (Bukkit.getServer().getBukkitVersion().split("-")[0]) {
            case "1.20.1" -> MC_1_20_R1_MessageFactory.getInstance();
            case "1.20.3", "1.20.4" -> MC_1_20_R3_MessageFactory.getInstance();
            default -> throw new UnsupportedVersionException("您的 Minecraft 版本不兼容，请使用合适的版本");
        };
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
    public void sendMessage(@NotNull MagicRealmsPlugin plugin,
                            @NotNull CommandSender receiver,
                            @NotNull String message) {
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
    public void sendBroadcast(@NotNull MagicRealmsPlugin plugin, @NotNull String msg) {
        Bukkit.getOnlinePlayers().forEach(e -> {
            if (e == null || e.getPlayer() == null) return;
            sendMessage(plugin, e.getPlayer(), msg);
        });
    }

    @Override
    public void sendBungeeMessage(@NotNull IRedisStore store,
                                  @NotNull String channel, @NotNull String player, @NotNull String msg) {
        BungeeMessage message = new BungeeMessage(player, msg);
        store.publishValue(channel, JsonUtil.objectToJson(message));
    }

    @Override
    public void sendBungeeBroadcast(@NotNull IRedisStore store, @NotNull String channel, @NotNull String msg) {
        BungeeMessage message = new BungeeMessage(msg);
        store.publishValue(channel, JsonUtil.objectToJson(message));
    }


}
