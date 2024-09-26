package com.magicrealms.magiclib.common.manage;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.command.annotations.Event;
import com.magicrealms.magiclib.common.command.processor.AppContext;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2024-09-26
 */

public class EventManager {

    private final AppContext APP_CONTEXT;

    private final MagicRealmsPlugin PLUGIN;

    private final Listener listener;

    public EventManager(@NotNull MagicRealmsPlugin plugin, @NotNull AppContext appContext) {
        this.APP_CONTEXT = appContext;
        this.PLUGIN = plugin;
        this.listener = new Listener() {};
    }

    public void registerEvents() {
        APP_CONTEXT.getMethodHashMap().keySet()
                .forEach(m -> getEventClass(m).ifPresent(
                        c ->  Arrays.stream(m.getAnnotations())
                                .filter(this::isEventAnnotation)
                                .forEach(annotation ->
                                        Bukkit.getPluginManager().registerEvent(
                                                c,
                                                listener,
                                                getEventPriority(annotation),
                                                EventExecutor.create(m, c),
                                                PLUGIN))));
    }

    public void unregisterEvents() {
        HandlerList.unregisterAll(listener);
    }

    private boolean isEventAnnotation(Annotation annotation) {
        return annotation instanceof Event;
    }

    @SuppressWarnings("unchecked")
    private Optional<Class<? extends org.bukkit.event.Event>> getEventClass(Method method) {
        if (!method.isAnnotationPresent(Event.class) ||
                method.getParameterCount() != 1
                || !org.bukkit.event.Event.class.isAssignableFrom(method.getParameterTypes()[0])
        ) {
            return Optional.empty();
        }
        return Optional.of((Class<? extends org.bukkit.event.Event>) method.getParameterTypes()[0]);
    }

    private EventPriority getEventPriority(Annotation annotation) {
        if (annotation instanceof Event) {
            return ((Event) annotation).priority();
        }
        return EventPriority.NORMAL;
    }
}
