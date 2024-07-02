package com.magicrealms.magiclib.common.dispatcher;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 事件调度器
 * @date 2024-05-10
 **/
@SuppressWarnings("unused")
public interface IEventDispatcher {

    /**
     * 调起一个事件，并给予回调
     * @param plugin 插件同步执行回调
     * @param event 事件
     * @param callback 成功执行回调
     * @param <T> 事件
     */
    default  <T extends Event> void dispatcherEvent(@NotNull Plugin plugin, @NotNull T event, @Nullable Consumer<T> callback) {
        if (eventIsExecute(event) && callback != null) {
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(event));
        }
    }

    /**
     * Bukkit 调起事件并捕获该事件是否被取消
     * @param event 事件
     * @return 是否被取消
     * @param <T> 事件
     */
    default  <T extends Event> boolean eventIsExecute(@NotNull T event) {
        Bukkit.getPluginManager().callEvent(event);
        return !(event instanceof Cancellable cancellable) || !cancellable.isCancelled();
    }
}
