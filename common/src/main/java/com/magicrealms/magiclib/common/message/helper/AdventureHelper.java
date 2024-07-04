package com.magicrealms.magiclib.common.message.helper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class AdventureHelper {
    private static volatile AdventureHelper INSTANCE;
    private final MiniMessage MINE_MESSAGE;
    private final MiniMessage MINI_MESSAGE_STRICT;

    private final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER;
    private final GsonComponentSerializer GSON_COMPONENT_SERIALIZER;

    private AdventureHelper() {
        this.MINE_MESSAGE = MiniMessage.builder().build();
        this.MINI_MESSAGE_STRICT = MiniMessage.builder().strict(true).build();
        this.LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer.builder()
                .hexColors()
                .character('§')
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
        this.GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.builder().build();
    }

    private static AdventureHelper getInstance() {
        if (INSTANCE == null) {
            synchronized (AdventureHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AdventureHelper();
                }
            }
        }
        return INSTANCE;
    }

    public static MiniMessage getMiniMessage() {
        return getInstance().MINE_MESSAGE;
    }

    public static MiniMessage getStrictMiniMessage() {
        return getInstance().MINI_MESSAGE_STRICT;
    }

    public static GsonComponentSerializer getGson() {
        return getInstance().GSON_COMPONENT_SERIALIZER;
    }

    public static @NotNull String legacyToMiniMessage(@NotNull String legacy) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = legacy.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!isColorCode(chars[i])) {
                stringBuilder.append(chars[i]);
                continue;
            }
            if (i + 1 >= chars.length) {
                stringBuilder.append(chars[i]);
                continue;
            }
            switch (chars[i+1]) {
                case '0' -> stringBuilder.append("<black>");
                case '1' -> stringBuilder.append("<dark_blue>");
                case '2' -> stringBuilder.append("<dark_green>");
                case '3' -> stringBuilder.append("<dark_aqua>");
                case '4' -> stringBuilder.append("<dark_red>");
                case '5' -> stringBuilder.append("<dark_purple>");
                case '6' -> stringBuilder.append("<gold>");
                case '7' -> stringBuilder.append("<gray>");
                case '8' -> stringBuilder.append("<dark_gray>");
                case '9' -> stringBuilder.append("<blue>");
                case 'a', 'A' -> stringBuilder.append("<green>");
                case 'b', 'B' -> stringBuilder.append("<aqua>");
                case 'c', 'C' -> stringBuilder.append("<red>");
                case 'd', 'D' -> stringBuilder.append("<light_purple>");
                case 'e', 'E' -> stringBuilder.append("<yellow>");
                case 'f', 'F' -> stringBuilder.append("<white>");
                case 'r', 'R' -> stringBuilder.append("<r><!i>");
                case 'l', 'L' -> stringBuilder.append("<b>");
                case 'm', 'M' -> stringBuilder.append("<st>");
                case 'o', 'O' -> stringBuilder.append("<i>");
                case 'n', 'N' -> stringBuilder.append("<u>");
                case 'k', 'K' -> stringBuilder.append("<obf>");
                case 'x', 'X' -> {
                    if (i + 13 >= chars.length
                            || !isColorCode(chars[i+2])
                            || !isColorCode(chars[i+4])
                            || !isColorCode(chars[i+6])
                            || !isColorCode(chars[i+8])
                            || !isColorCode(chars[i+10])
                            || !isColorCode(chars[i+12])) {
                        stringBuilder.append(chars[i]);
                        continue;
                    }
                    stringBuilder
                            .append("<#")
                            .append(chars[i+3])
                            .append(chars[i+5])
                            .append(chars[i+7])
                            .append(chars[i+9])
                            .append(chars[i+11])
                            .append(chars[i+13])
                            .append(">");
                    i += 12;
                }
                default -> {
                    stringBuilder.append(chars[i]);
                    continue;
                }
            }
            i++;
        }
        return stringBuilder.toString();
    }

    public static @NotNull Component deserializeComponent(@NotNull String serializeStr) {
        return getMiniMessage().deserialize(serializeStr);
    }

    public static @NotNull String serializeComponent(@NotNull Component component) {
        return getGson().serialize(component);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isColorCode(char c) {
        return c == '§' || c == '&';
    }
}
