package com.magicrealms.magiclib.core.advance;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.core.MagicLib;
import com.magicrealms.magiclib.core.advance.entity.AdvanceConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.magicrealms.magiclib.core.MagicLibConstant.YML_ADVANCE;

/**
 * @author Ryan-0916
 * @Desc 字符偏移度加载器
 * @date 2025-05-08
 */
@Slf4j
@SuppressWarnings("unused")
public class AdvanceLoader {

    private static final Pattern KEY_PATTERN = Pattern.compile("^([a-z0-9_.-]+:)?[a-z0-9_./-]+$");

    private final ConfigManager configManager;

    @Getter
    private final Map<Key, AdvanceConfig> advanceConfig = new HashMap<>();

    private boolean isValidKey(String key) {
        return key != null && KEY_PATTERN.matcher(key).matches();
    }

    public AdvanceLoader(MagicLib plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllFontAdvance();
    }

    private void loadAllFontAdvance() {
        configManager
                .getYmlSubKeys(YML_ADVANCE, "Advance", false)
                .ifPresent(keys -> keys.forEach(this::loadAdvanceConfig));
    }

    private void loadAdvanceConfig(String configKey) {
        String basePath = "Advance." + configKey;
        // 1. 加载 Font Key，并验证格式
        Set<NamespacedKey> fontValue = configManager.getYmlListValue(YML_ADVANCE, basePath + ".Font")
                .map(list -> list.stream()
                        .map(f -> Optional.ofNullable(NamespacedKey.fromString(f)))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toCollection(HashSet::new)))
                        .orElseGet(() -> new HashSet<>(Collections.singleton(NamespacedKey.minecraft("default"))));

        if (fontValue.isEmpty()) {
            return;
        }

        // 2. 加载 Character 宽度映射
        Map<Integer, Integer> charMap = new HashMap<>();
        configManager
                .getYmlSubKeys(YML_ADVANCE, basePath + ".Character", false)
                .ifPresent(keys -> keys.forEach(
                        key -> {
                            int width = configManager.getYmlValue(
                                    YML_ADVANCE,
                                    basePath + ".Character." + key,
                                    0,
                                    ParseType.INTEGER
                            );
                            /* key 转换 Unicode*/
                            if (key != null && !key.isEmpty()) {
                                char ch;
                                try {
                                     ch = key.startsWith("\\u")
                                            ? (char) Integer.parseInt(key.substring(2), 16)
                                            : key.charAt(0);
                                } catch (Exception e) {
                                    ch = key.charAt(0);
                                }
                                charMap.put((int) ch, width);
                            }
                        }
                ));

        fontValue.forEach(e -> advanceConfig.put(e, AdvanceConfig.builder()
                .font(e)
                .defaultWidth(configManager.getYmlValue(
                        YML_ADVANCE,
                        basePath + ".Default",
                        0,
                        ParseType.INTEGER
                ))
                .charWidthMap(charMap)
                .build()));
    }


}
