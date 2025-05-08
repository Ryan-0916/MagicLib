package com.magicrealms.magiclib.core.advance.entity;

import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.key.Key;

import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc Advance
 * @date 2025-05-08
 */
@Getter
@Builder
public class AdvanceConfig {
    private Key font;
    private int defaultWidth;
    private Map<Integer, Integer> charWidthMap;
}
