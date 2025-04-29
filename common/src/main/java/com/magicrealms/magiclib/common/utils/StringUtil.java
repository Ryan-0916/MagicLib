package com.magicrealms.magiclib.common.utils;

import com.magicrealms.magiclib.common.enums.ParseType;
import lombok.extern.slf4j.Slf4j;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 常用字符串处理工具类
 * @date 2024-05-06
 */
@Slf4j
@SuppressWarnings("unused")
public final class StringUtil {

    public static final String EMPTY = "";
    private static final String PREFIX_DELIMITER = "::";
    private static final String PLACEHOLDER_FORMAT = "%%%s%%";
    private static final Pattern TAG_CONTENT_PATTERN
            = Pattern.compile("<([^>]+)>([^<]*)");
    private static final Pattern PLACEHOLDER_PATTERN
            = Pattern.compile("%(.+?)%");

    private StringUtil() {}

    /**
     * 提取字符串中指定标签之间的内容
     * @param str 要搜索的字符串，可为 null
     * @param tag 要查找的标签，可为 null 或空
     * @return 包含提取文本的 Optional 对象，未找到则返回 empty
     */
    public static Optional<String> getStringBetweenTags(String str, String tag) {
        if (isEmpty(str) || isEmpty(tag)) {
            return Optional.empty();
        }
        String regex = String.format("<%s>(.*?)</%s>", Pattern.quote(tag), Pattern.quote(tag));
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(str);
        return matcher.find() ?
                Optional.ofNullable(matcher.group(1)).map(String::trim)
                : Optional.empty();
    }

    /**
     * 提取标签间内容并解析为指定类型
     * @param str 要搜索的字符串
     * @param tag 要查找的标签
     * @param defaultValue 解析失败时的默认值
     * @param valueType 目标解析类型
     * @param <T> 返回值类型
     * @return 解析后的值，失败则返回默认值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueBetweenTags(String str, String tag, T defaultValue, ParseType valueType) {
        return getStringBetweenTags(str, tag)
                .map(text -> {
                    try {
                        return (T) valueType.parse(text);
                    } catch (Exception e) {
                        log.error("无法将文本'{}'解析为{}类型", text, valueType.getType().getSimpleName(), e);
                        return defaultValue;
                    }
                })
                .orElse(defaultValue);
    }

    /**
     * 将含标签的字符串解析为标签-值列表
     * @param str 要解析的字符串
     * @return 标签-值列表(格式: tag::value)，若无标签则返回"prefix::原字符串"
     */
    public static List<String> parseTagsToList(String str) {
        if (isEmpty(str)) {
            return Collections.emptyList();
        }
        Matcher matcher = TAG_CONTENT_PATTERN.matcher(str);
        if (!matcher.find()) {
            return Collections.singletonList("prefix" + PREFIX_DELIMITER + str);
        }
        List<String> result = new ArrayList<>();
        matcher.reset();
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                result.add("prefix" + PREFIX_DELIMITER + str.substring(lastEnd, matcher.start()));
            }
            String tag = matcher.group(1);
            String content = matcher.group(2);
            result.add(isEmpty(content) ? tag : tag + PREFIX_DELIMITER + content);
            lastEnd = matcher.end();
        }
        if (lastEnd < str.length()) {
            result.add("prefix" + PREFIX_DELIMITER + str.substring(lastEnd));
        }
        return result;
    }

    /**
     * 移除字符串中指定标签及其内容
     * @param str 原始字符串
     * @param tag 要移除的标签
     * @return 移除标签后的字符串，输入为null时返回原字符串
     */
    public static String removeTag(String str, String tag) {
        if (str == null || isEmpty(tag)) {
            return str;
        }
        return replaceAll(str, String.format("<%s>.*?</%s>", tag, tag), EMPTY);
    }

    /**
     * 移除字符串中多个标签及其内容
     * @param str 原始字符串
     * @param tags 要移除的标签数组
     * @return 移除标签后的字符串，输入为null时返回原字符串
     */
    public static String removeTags(String str, String... tags) {
        if (str == null || tags == null) {
            return str;
        }
        for (String tag : tags) {
            if (tag != null) {
                str = removeTag(str, tag);
            }
        }
        return str;
    }

    /**
     * 替换字符串中所有匹配正则的内容(不区分大小写)
     * @param str 原始字符串
     * @param regex 正则表达式
     * @param replacement 替换内容
     * @return 替换后的字符串，输入为null时返回原字符串
     */
    public static String replaceAll(String str, String regex, String replacement) {
        if (str == null || regex == null || replacement == null) {
            return str;
        }
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(str).replaceAll(replacement);
    }

    /**
     * 使用Map中的值替换字符串中的占位符
     * @param str 原始字符串
     * @param replacements 占位符-值映射表
     * @return 替换后的字符串，输入为null时返回原字符串
     */
    public static String replacePlaceholders(String str, Map<String, String> replacements) {
        if (str == null || replacements == null || replacements.isEmpty()) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(str);
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = replacements.getOrDefault(placeholder, matcher.group());
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 替换字符串中的单个占位符
     * @param str 原始字符串
     * @param placeholder 要替换的占位符(不带%)
     * @param replacement 替换内容
     * @return 替换后的字符串，输入为null时返回原字符串
     */
    public static String replacePlaceholder(String str, String placeholder, String replacement) {
        if (str == null || placeholder == null || replacement == null) {
            return str;
        }
        return str.replaceAll(String.format(PLACEHOLDER_FORMAT, Pattern.quote(placeholder)),
                Matcher.quoteReplacement(replacement));
    }

    /**
     * 检查字符串是否为 null 或空
     * @param str 要检查的字符串
     * @return 如果为 null 或空返回 true
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
