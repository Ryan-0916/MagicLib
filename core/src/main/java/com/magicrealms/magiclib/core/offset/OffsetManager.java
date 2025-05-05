package com.magicrealms.magiclib.core.offset;

import com.magicrealms.magiclib.core.MagicLib;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ryan-0916
 * @Desc 偏移量管理器
 * @date 2025-05-05
 */
public class OffsetManager {

    private final OffsetLoader offsetLoader;

    public OffsetManager(MagicLib plugin) {
        this.offsetLoader = new OffsetLoader(plugin);
    }

    public String format(int offset, String text) {
        int offsetAbs = Math.abs(offset);
        /* 判断是否是 2 的幂次方：2^0 到 2^8 (即 1 到 256) */
        if (offset == 0) { return text; }
        List<Integer> components = calculateOffset(offsetAbs)
                .stream().map(e -> offset < 0 ? -e : e)
                .toList();
        StringBuilder result = new StringBuilder();
        for (Integer component : components) {
            result.append(offsetLoader.getOffsets().get(component));
        }
        result.append(text);
        return result.toString();
    }

    public static List<Integer> calculateOffset(int num) {
        List<Integer> result = new ArrayList<>();
        int remaining = num;
        while (remaining >= 256) {
            result.add(256);
            remaining -= 256;
        }
        for (int power = 128; power >= 1; power >>= 1) {
            if (remaining >= power) {
                result.add(power);
                remaining -= power;
            }
            if (remaining == 0) break;
        }
        return result;
    }
}
