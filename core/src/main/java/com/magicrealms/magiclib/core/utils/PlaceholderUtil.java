package com.magicrealms.magiclib.core.utils;

import com.magicrealms.magiclib.common.utils.StringUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ryan-0916
 * @Desc 变量相关工具类
 * @date 2025-05-06
 */
@SuppressWarnings("unused")
public final class PlaceholderUtil {

    private static final String PLACEHOLDER_PLUGIN = "PlaceholderAPI";

    private PlaceholderUtil() {}

    /**
     * 替换文本中的所有变量
     * @param text 原始文本
     * @param player 玩家（用于PAPI变量）
     * @return 替换后的文本
     */
    public static String replacePlaceholders(String text, OfflinePlayer player) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        if (player == null) {
            return text;
        }
        if (!Bukkit.getPluginManager().isPluginEnabled(PLACEHOLDER_PLUGIN)) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }


    public static String replacePlaceholders(String text,
                                             Map<String, String> placeholders,
                                             OfflinePlayer player) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        if (placeholders == null || placeholders.isEmpty()) {
            return replacePlaceholders(text, player);
        }
        if (player == null) {
            return StringUtil.replacePlaceholders(text, placeholders);
        }
        return replacePlaceholders(StringUtil.replacePlaceholders(text, placeholders), player);
    }

    public static List<String> replacePlaceholders(List<String> text, OfflinePlayer player) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.stream().map(e -> replacePlaceholders(e, player))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<String> replacePlaceholders(List<String> text, Map<String, String> placeholders,
                                                   OfflinePlayer player) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.stream().map(e -> replacePlaceholders(e, placeholders, player))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
