package com.magicrealms.magiclib.mc_1_21_R3.message.factory;

import com.magicrealms.magiclib.bukkit.message.AbstractMessage;
import com.magicrealms.magiclib.bukkit.message.enums.MessageType;
import com.magicrealms.magiclib.bukkit.message.factory.IMessageFactory;
import com.magicrealms.magiclib.mc_1_21_R3.message.*;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-04-01
 */
@SuppressWarnings("unused")
public class MC_1_21_R3_MessageFactory implements IMessageFactory {
    private static volatile MC_1_21_R3_MessageFactory INSTANCE;

    private MC_1_21_R3_MessageFactory() {}

    public static MC_1_21_R3_MessageFactory getInstance() {
        if (INSTANCE == null) {
            synchronized(MC_1_21_R3_MessageFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MC_1_21_R3_MessageFactory();
                }
            }
        }
        return INSTANCE;
    }

    public AbstractMessage create(MessageType e) {
        return switch (e) {
            case MESSAGE -> Message.getInstance();
            case TITLE -> TitleMessage.getInstance();
            case BOSS_BAR -> BossBarMessage.getInstance();
            case ACTION_BAR -> ActionBarMessage.getInstance();
            case SOUND -> SoundMessage.getInstance();
            case TOAST -> ToastMessage.getInstance();
            case TOTEM -> TotemMessage.getInstance();
        };
    }
}
