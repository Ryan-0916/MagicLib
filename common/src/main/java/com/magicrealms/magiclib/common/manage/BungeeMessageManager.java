package com.magicrealms.magiclib.common.manage;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.bungee.BungeeMessage;
import com.magicrealms.magiclib.common.utils.JsonUtil;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class BungeeMessageManager extends JedisPubSub {
    private final MagicRealmsPlugin PLUGIN;
    private final Consumer<BungeeMessage> MESSAGE_LISTENER;
    private final Consumer<String> SUBSCRIBE_LISTENER;
    private final Consumer<String> UNSUBSCRIBE_LISTENER;
    private BukkitTask bukkitTask;
    private Jedis connection;

    private BungeeMessageManager(Builder builder) {
        this.PLUGIN = builder.plugin;
        this.MESSAGE_LISTENER = builder.messageListener;
        this.SUBSCRIBE_LISTENER = builder.subscribeListener;
        this.UNSUBSCRIBE_LISTENER = builder.unSubscribeListener;

        if (PLUGIN == null) {
            throw new NullPointerException("SubScribe message plugin is null");
        }

        if (MESSAGE_LISTENER == null) {
            throw new NullPointerException("SubScribe message listener is null");
        }

        if (builder.channel == null) {
            throw new NullPointerException("SubScribe channel is null");
        }

        if (builder.host == null) {
            throw new NullPointerException("Redis host is null");
        }

        if (builder.password == null) {
            throw new NullPointerException("Redis password is null");
        }

        try {
            connection = new Jedis(builder.host, builder.port, 60000);
            connection.auth(builder.password);
            subscribe(builder.channel);
        } catch (Exception e) {
            PLUGIN.getLoggerManager().warning("订阅 Redis 服务异常");
        }

    }

    public void subscribe(String channel) {
        BungeeMessageManager pubSub = this;
        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                connection.subscribe(pubSub, channel);
            }
        }.runTaskAsynchronously(PLUGIN);
    }

    @Override
    public void unsubscribe() {
        if (bukkitTask != null && !bukkitTask.isCancelled()) {
            bukkitTask.cancel();
        }

        if (super.isSubscribed()) {
            super.unsubscribe();
        }
    }

    /**
     * 当取到消息后的处理
     * @param channel 频道名
     * @param message 消息
     */
    @Override
    public void onMessage(String channel, String message) {
        /* 取得订阅的消息后的处理 */
        BungeeMessage bungeeMessage = JsonUtil.jsonToObject(message, BungeeMessage.class);
        MESSAGE_LISTENER.accept(bungeeMessage);
    }

    /**
     * 初始化订阅时候的处理
     * @param channel 频道名
     * @param subscribedChannels 订阅数量
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        /* 初始化订阅时候的处理 */
        if (SUBSCRIBE_LISTENER == null) {
            return;
        }
        SUBSCRIBE_LISTENER.accept(channel);
    }

    /**
     * 取消订阅时候的处理
     * @param channel 频道名
     * @param subscribedChannels 订阅数量
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable task = () -> {
            if (connection != null && connection.isConnected()) {
                connection.close();
            }
        };
        executorService.schedule(task, 1, TimeUnit.SECONDS);
        executorService.shutdown();
        /* 取消订阅时候的处理 */
        if (UNSUBSCRIBE_LISTENER == null) {
            return;
        }
        UNSUBSCRIBE_LISTENER.accept(channel);
    }

    public static class Builder {
        private MagicRealmsPlugin plugin;
        private String channel;
        private String host;
        private int port;
        private String password;
        private Consumer<BungeeMessage> messageListener;
        private Consumer<String> subscribeListener;
        private Consumer<String> unSubscribeListener;

        public Builder plugin(MagicRealmsPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder messageListener(Consumer<BungeeMessage> messageListener) {
            this.messageListener = messageListener;
            return this;
        }

        public Builder subscribeListener(Consumer<String> subscribeListener) {
            this.subscribeListener = subscribeListener;
            return this;
        }

        public Builder unSubscribeListener(Consumer<String> unSubscribeListener) {
            this.unSubscribeListener = unSubscribeListener;
            return this;
        }

        public BungeeMessageManager build() {
            return new BungeeMessageManager(this);
        }
    }

}
