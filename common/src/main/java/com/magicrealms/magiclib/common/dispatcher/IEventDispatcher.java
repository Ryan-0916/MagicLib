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
 * @Desc 事件调度器接口，用于处理 Bukkit 插件中的事件调度
 * @date 2024-05-10
 */
@SuppressWarnings("unused")
public interface IEventDispatcher {

    /**
     * 调起一个事件，并异步给予回调
     * @param plugin 插件，用于调度任务的执行者
     * @param event 需要调度的事件
     * @param callback 事件成功执行后的回调函数
     * @param <T> 事件类型，必须是 {@link Event} 的子类
     */
    default  <T extends Event> void dispatcherEvent(@NotNull Plugin plugin, @NotNull T event, @Nullable Consumer<T> callback) {
        if (eventIsExecute(event) && callback != null) {
            Bukkit.getScheduler().runTask(plugin, () -> callback.accept(event));
        }
    }

    /**
     * Bukkit 调起一个事件，并检查该事件是否被取消
     * @param event 需要调度的事件
     * @return 如果事件没有被取消，返回 true；否则返回 false
     * @param <T> 事件类型，必须是 {@link Event} 的子类
     */
    default  <T extends Event> boolean eventIsExecute(@NotNull T event) {
        Bukkit.getPluginManager().callEvent(event);
        return !(event instanceof Cancellable cancellable) || !cancellable.isCancelled();
    }
}
