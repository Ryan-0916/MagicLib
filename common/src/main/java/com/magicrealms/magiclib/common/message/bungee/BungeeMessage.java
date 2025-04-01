package com.magicrealms.magiclib.common.message.bungee;

import com.magicrealms.magiclib.common.message.enums.SendType;
import lombok.Data;


@Data
@SuppressWarnings("unused")
public class BungeeMessage {

    private SendType type;
    private String message;
    private String recipientName;

    public BungeeMessage(String recipientName, String message) {
        this.type = SendType.PLAYER_MESSAGE;
        this.recipientName = recipientName;
        this.message = message;
    }

    public BungeeMessage(String message) {
        this.type = SendType.SERVER_MESSAGE;
        this.message = message;
    }
}
