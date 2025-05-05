package com.magicrealms.magiclib.core.offset;

import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.common.utils.StringUtil;
import com.magicrealms.magiclib.core.MagicLib;
import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

import static com.magicrealms.magiclib.core.MagicLibConstant.YML_OFFSET;

/**
 * @author Ryan-0916
 * @Desc 偏移量配置文件加载器
 * @date 2025-05-05
 */
public class OffsetLoader {

    private final ConfigManager configManager;

    @Getter
    private final Map<Integer, String> offsets = new HashMap<>();

    public OffsetLoader(MagicLib plugin) {
        this.configManager = plugin.getConfigManager();
        this.loadAllOffsetConfig();
    }

    private void loadAllOffsetConfig() {
        String font = configManager
                .getYmlValue(YML_OFFSET, "Offset.Font", "minecraft:default",
                        ParseType.STRING);
        String formatTag = String.format("<font:%s>", font) + "%s</font>";


        for (int i = 1; i <= 256; i *= 2) {
            String positive
                    = configManager.getYmlValue(YML_OFFSET, "Offset." + i, StringUtil.EMPTY, ParseType.STRING);
            String negative
                    = configManager.getYmlValue(YML_OFFSET, "Offset." + -i, StringUtil.EMPTY, ParseType.STRING);
            offsets.put(i, String.format(formatTag, positive));
            offsets.put(-i, String.format(formatTag, negative));
        }
    }
}
