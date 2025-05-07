package com.magicrealms.magiclib.mc_1_21_R3.utils;

import com.magicrealms.magiclib.bukkit.message.helper.AdventureHelper;
import net.minecraft.network.chat.Component;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc Component 处理类
 * @date 2025-04-03
 */
public class ComponentUtil {

    public static Component getComponentOrEmpty(String json) {
        return Optional.ofNullable(CraftChatMessage.fromJSONOrNull(json)).orElse(Component.empty());
    }


    @NotNull
    public static Component formMiniMessage(String miniMessage) {
        return formMiniMessage(miniMessage, false);
    }

    @NotNull
    public static Component formMiniMessage(String miniMessage, boolean legacy) {
        return getComponentOrEmpty(serializeComponent(miniMessage, legacy));
    }

    @NotNull
    public static String serializeComponent(String content, boolean legacy) {
        return AdventureHelper.getGson().serialize(
                AdventureHelper.deserializeComponent(legacy ? AdventureHelper.legacyToMiniMessage(content) : content));
    }

}
