package com.magicrealms.magiclib.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 字符串工具类
 * @date 2024-05-06
 */
@SuppressWarnings("unused")
public class StringUtil {

    public static final String EMPTY = "";

    /**
     * 获取标签之间的字符串
     * @param str 原文本
     * @param tag 标签名称
     * @return 返回第一个嵌套在标签之间的字符串，如果未找到则返回空 Optional {@link Optional}
     */
    public static Optional<String> getStringBTWTags(String str, String tag) {
        if (str == null || tag == null || tag.isEmpty()) {
            return Optional.empty();
        }
        Pattern pattern = Pattern.compile("<" + Pattern.quote(tag) + ">(.*?)</" + Pattern.quote(tag) + ">", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Optional.of(matcher.group(1).trim());
        }
        return Optional.empty();
    }

    /**
     * 获取标签之间的整数
     * @param str 原文本
     * @param tag 标签名称
     * @return 返回第一个嵌套在标签之间的整数，如果未找到则返回默认值
     */
    public static int getIntegerBTWTags(String str, String tag, int def) {
        Optional<String> text = getStringBTWTags(str, tag);
        if (text.isEmpty()) {
            return def;
        }
        try {
            return Integer.parseInt(text.get());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取标签之间的长整数
     * @param str 原文本
     * @param tag 标签名称
     * @return 返回第一个嵌套在标签之间的整数，如果未找到则返回默认值
     */
    public static long getLongBTWTags(String str, String tag, long def) {
        Optional<String> text = getStringBTWTags(str, tag);
        if (text.isEmpty()) {
            return def;
        }
        try {
            return Long.parseLong(text.get());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取标签之间的浮点数
     * @param str 原文本
     * @param tag 标签名称
     * @return 返回第一个嵌套在标签之间的浮点数，如果未找到则返回默认值
     */
    public static float getFloatBTWTags(String str, String tag, float def) {
        Optional<String> text = getStringBTWTags(str, tag);
        if (text.isEmpty()) {
            return def;
        }
        try {
            return Float.parseFloat(text.get());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取标签之间的浮点数
     * @param str 原文本
     * @param tag 标签名称
     * @return 返回第一个嵌套在标签之间的浮点数，如果未找到则返回默认值
     */
    public static double getDoubleBTWTags(String str, String tag, double def) {
        Optional<String> text = getStringBTWTags(str, tag);
        if (text.isEmpty()) {
            return def;
        }
        try {
            return Double.parseDouble(text.get());
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 获取标签之间的 boolean
     * @param str 原文本
     * @param tag 标签名称
     * @return 返回第一个嵌套在标签之间的 boolean，如果未找到则返回默认值
     */
    public static boolean getBooleanBTWTags(String str, String tag, boolean def) {
        Optional<String> text = getStringBTWTags(str, tag);
        if (text.isEmpty()) {
            return def;
        }
        try {
            return Boolean.parseBoolean(text.get());
        } catch (Exception e) {
            return def;
        }
    }


    public static List<String> getTagsToList(String str) {
        Pattern pattern = Pattern.compile("<([^>]+)>([^<]*)");
        if (!pattern.matcher(str).find()) {
            return List.of("prefix::" + str);
        }
        Matcher matcher = pattern.matcher(str);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            if (matcher.start() != 0) {
                list.add("prefix::" + str.substring(0, matcher.start()));
            }
            list.add(!matcher.group(2).isEmpty() ? matcher.group(1) + "::" + matcher.group(2) : matcher.group(1));
            str = str.substring(matcher.end());
            matcher = pattern.matcher(str);
        }
        return list;
    }

    /**
     * 获取标签并返回移除标签之后的字符串
     * @param str 原文本
     * @param tag 需要移除的标签
     * @return 返回移除所有标签后的文本
     */
    public static String removeTag(String str, String tag) {
        if (str == null || tag == null) {
            return str;
        }
        return replaceAll(str, "<" + tag + ">.*</" + tag + ">", "");
    }

    /**
     * 移除全部标签
     * @param str 原文
     * @param tags 标签
     * @return 移除全部标签后的文本
     */
    public static String removeTags(String str, String... tags) {
        if (str == null || tags == null) {
            return str;
        }
        for (String tag : tags) {
            str = removeTag(str, tag);
        }
        return str;
    }

    /**
     * 替换所有满足规则的字符串，不区分大小写
     * @param str 原文本
     * @param regex 正则规则
     * @param replacement 需要替换成为什么字符
     * @return 返回替换后的文本
     */
    public static String replaceAll(String str, String regex, String replacement) {
        if (str == null || regex == null || replacement == null) {
            return str;
        }
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(str).replaceAll(replacement);
    }

    /**
     * 替换所有满足的占位符
     * @param str 原文本
     * @param map 需要替换的占位符列表
     * @return 返回替换后的文本
     */
    public static String replacePlaceholder(String str, Map<String, String> map) {
        if (str == null || map == null) {
            return str;
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            str = replacePlaceholder(str, entry.getKey(), entry.getValue());
        }

        return str;
    }

    /**
     * 替换占位符
     * @param str 原文本
     * @param placeholder 需要替换的占位符
     * @param replacement 替换后的占位符
     * @return 返回替换后的文本
     */
    public static String replacePlaceholder(String str, String placeholder, String replacement) {
        if (str == null || placeholder == null || replacement == null) {
            return str;
        }

        return replaceAll(str, "%" + placeholder + "%", replacement);
    }
}
