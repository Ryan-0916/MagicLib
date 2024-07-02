package com.magicrealms.magiclib.mc_1_20_R3.message;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.message.AbstractMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Ryan-0916
 * @Desc 消息栏消息
 * @date 2024-05-17
 **/
public class BossBarMessage extends AbstractMessage {

    private static volatile BossBarMessage INSTANCE;

    private BossBarMessage() {}

    public static BossBarMessage getInstance() {
        if (INSTANCE == null) {
            synchronized (BossBarMessage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BossBarMessage();
                }
            }
        }
        
        return INSTANCE;
    }


    @Override
    public void sendMessage(@NotNull MagicRealmsPlugin plugin, @NotNull Player player, @NotNull String message) {
    }

    @Override
    public void cleanMessage(@NotNull Player player) {

    }
}
