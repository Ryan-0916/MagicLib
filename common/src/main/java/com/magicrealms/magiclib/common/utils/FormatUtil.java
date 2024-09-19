package com.magicrealms.magiclib.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ryan-0916
 * @Desc 格式化工具类
 * @date 2024-06-20
 */
@SuppressWarnings("unused")
public class FormatUtil {
    private static final DecimalFormat amountFormat = new DecimalFormat("#.##");
    public static String amountFormat (BigDecimal amount) { return amountFormat.format(amount); }
    public static String amountFormat (double amount) { return amountFormat.format(amount); }
    public static String amountFormat (float amount) { return amountFormat.format(amount); }
    public static @NotNull Map<String, String> datetimeFormat (long time, @Nullable String prefix) {
        Map<String, String> format = new HashMap<>();
        if (time <= 0) return format;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH) + 1,
                    day = calendar.get(Calendar.DAY_OF_MONTH),
                    hour = calendar.get(Calendar.HOUR_OF_DAY),
                    minute = calendar.get(Calendar.MINUTE),
                    second = calendar.get(Calendar.SECOND);
            boolean prefixBlank = StringUtils.isBlank(prefix);
            format.put(prefixBlank ? "yyyy" : prefix + "yyyy", String.valueOf(year));
            format.put(prefixBlank ? "yy" : prefix + "yy", String.valueOf(year).substring(2));
            format.put(prefixBlank ? "MM" : prefix + "MM", String.format("%02d", month));
            format.put(prefixBlank ? "dd" : prefix + "dd", String.format("%02d", day));
            format.put(prefixBlank ? "HH" : prefix + "HH", String.format("%02d", hour));
            format.put(prefixBlank ? "mm" : prefix + "mm", String.format("%02d", minute));
            format.put(prefixBlank ? "ss" : prefix + "ss", String.format("%02d", second));
            return format;
        } catch (Exception e) {
            return format;
        }
    }
}
