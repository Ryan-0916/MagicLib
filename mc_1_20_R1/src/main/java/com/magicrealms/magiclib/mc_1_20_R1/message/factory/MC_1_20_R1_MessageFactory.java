package com.magicrealms.magiclib.mc_1_20_R1.message.factory;

import com.magicrealms.magiclib.common.message.AbstractMessage;
import com.magicrealms.magiclib.common.message.enums.MessageType;
import com.magicrealms.magiclib.common.message.factory.IMessageFactory;
import com.magicrealms.magiclib.mc_1_20_R1.message.*;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ryan-0916
 * @Desc 具体消息工厂实现类 - 对应 MC 版本 1.20.1
 * @date 2024-07-08
 */
public class MC_1_20_R1_MessageFactory implements IMessageFactory {


    private static volatile MC_1_20_R1_MessageFactory INSTANCE;

    private MC_1_20_R1_MessageFactory() {}

    public static MC_1_20_R1_MessageFactory getInstance() {
        if (INSTANCE == null) {
            synchronized(MC_1_20_R1_MessageFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MC_1_20_R1_MessageFactory();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public AbstractMessage create(@NotNull MessageType e) {
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
