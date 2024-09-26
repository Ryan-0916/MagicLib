package com.magicrealms.magiclib.common.command.annotations;

import org.bukkit.event.EventPriority;

import java.lang.annotation.*;

/**
 * @author Ryan-0916
 * @Desc
 * @date 2024-09-26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SuppressWarnings("unused")
public @interface Event {
    EventPriority priority() default EventPriority.NORMAL;
}
