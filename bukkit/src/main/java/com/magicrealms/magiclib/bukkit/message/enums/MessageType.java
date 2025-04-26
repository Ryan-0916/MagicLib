package com.magicrealms.magiclib.bukkit.message.enums;

import lombok.Getter;

/**
 * @author Ryan-0916
 * @Desc 枚举-消息类型
 * @date 2024-05-06
 */
@Getter
public enum MessageType {
    MESSAGE(0, "message"),
    TITLE(1, "title"),
    ACTION_BAR(2, "actionBar"),
    BOSS_BAR(3, "bossBar"),
    SOUND(4, "sound"),
    TOAST(5, "toast"),
    TOTEM(6, "totem");

    private final int value;

    private final String tag;

    MessageType(int value, String tag) {
        this.value = value;
        this.tag = tag;
    }
}
