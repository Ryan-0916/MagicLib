package com.magicrealms.magiclib.mc_1_20_R1.utils;

import net.minecraft.network.chat.Component;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc Component 处理类
 * @date 2025-04-03
 */
public class ComponentUtil {

    @NotNull
    public static Component getComponentOrEmpty(String content) {
        return Optional.ofNullable(CraftChatMessage.fromJSONOrNull(content)).orElse(Component.empty());
    }

}
