package com.magicrealms.magiclib.core.dispatcher;

import com.magicrealms.magiclib.bukkit.dispatcher.IEventDispatcher;

/**
 * @author Ryan-0916
 * @Desc 事件调度器
 * @date 2024-05-17
 */
@SuppressWarnings("unused")
public class EventDispatcher implements IEventDispatcher {

    private static volatile EventDispatcher INSTANCE;

    private EventDispatcher() {}

    public static EventDispatcher getInstance() {
        if (INSTANCE == null) {
            synchronized(EventDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EventDispatcher();
                }
            }
        }

        return INSTANCE;
    }

}
