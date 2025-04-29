package com.magicrealms.magiclib.common.utils;

import org.apache.commons.lang3.StringUtils;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 格式化处理工具类
 * @date 2024-06-20
 */
@SuppressWarnings("unused")
public final class FormatUtil {

    private FormatUtil() {}

    private static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#,##0.##");

    /**
     * 格式化BigDecimal金额
     * @param amount 金额数值，允许为 null
     * @return 格式化后的金额字符串，null 返回 "0"
     */
    public static String formatAmount(BigDecimal amount) {
        return amount != null ? AMOUNT_FORMAT.format(amount) : "0.00";
    }

    /**
     * 格式化double金额
     * @param amount 金额数值
     * @return 格式化后的金额字符串
     */
    public static String formatAmount(double amount) {
        return AMOUNT_FORMAT.format(amount);
    }

    /**
     * 格式化float金额
     * @param amount 金额数值
     * @return 格式化后的金额字符串
     */
    public static String formatAmount(float amount) {
        return AMOUNT_FORMAT.format(amount);
    }

    /**
     * 格式化时间戳为日期时间组件Map
     * @param timestamp 时间戳(毫秒)
     * @param prefix Map键前缀，可为null
     * @return 包含日期时间组件的不可变Map，无效输入返回空Map
     * <p>Map中包含以下键：yyyy(年)、yy(年后两位)、MM(月)、dd(日)、
     * HH(时)、mm(分)、ss(秒)，带前缀时键名为前缀+组件名</p>
     */
    public static Map<String, String> formatDateTime(long timestamp, @Nullable String prefix) {
        if (timestamp <= 0) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>(8);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            boolean hasPrefix = StringUtils.isNotBlank(prefix);
            putComponent(result, hasPrefix, prefix, "yyyy", String.valueOf(year));
            putComponent(result, hasPrefix, prefix, "yy", String.valueOf(year).substring(2));
            putComponent(result, hasPrefix, prefix, "MM", String.format("%02d", month));
            putComponent(result, hasPrefix, prefix, "dd", String.format("%02d", day));
            putComponent(result, hasPrefix, prefix, "HH", String.format("%02d", hour));
            putComponent(result, hasPrefix, prefix, "mm", String.format("%02d", minute));
            putComponent(result, hasPrefix, prefix, "ss", String.format("%02d", second));
            return Collections.unmodifiableMap(result);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private static void putComponent(Map<String, String> map, boolean hasPrefix,
                                     String prefix, String key, String value) {
        map.put(hasPrefix ? prefix + key : key, value);
    }
}
