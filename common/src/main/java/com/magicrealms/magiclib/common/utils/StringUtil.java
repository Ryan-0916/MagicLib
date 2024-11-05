package com.magicrealms.magiclib.common.utils;

import com.magicrealms.magiclib.common.enums.ParseType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 常用字符串工具类
 * @date 2024-05-06
 */
@Slf4j
@SuppressWarnings("unused")
public class StringUtil {

    public static final String EMPTY = "";

    /**
     * 从给定的字符串中提取位于指定标签之间的文本。
     * 该方法使用正则表达式来搜索字符串中匹配指定标签的内容，并返回匹配到的文本。
     * 如果未找到匹配项或输入参数无效（如字符串或标签为 {@code null} 或标签为空），则返回一个空的 {@link Optional}。
     * @param str 要搜索的字符串，可能为 {@code null}。
     * @param tag 用于定位文本的标签，可能为 {@code null} 或空字符串。
     * @return 一个包含提取文本的 {@link Optional}，如果未找到文本或输入参数无效，则返回一个空的 {@link Optional}。
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
     * 从给定的字符串中提取位于指定标签之间的文本，并尝试将其解析为指定的数据类型。
     * @param str 要搜索的字符串。
     * @param tag 用于定位文本的标签。
     * @param defaultValue 如果未找到标签或解析失败时返回的默认值。
     * @param valueType 用于解析文本的数据类型，必须是 {@link ParseType} 枚举中的一个常量。
     * @param <T> 返回值的类型，与 {@code valueType} 相对应。
     * @return 解析后的值，如果解析失败或未找到标签，则返回 {@code defaultValue}。
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueBTWTags(String str, String tag, T defaultValue, @NotNull ParseType valueType) {
        Optional<String> text = getStringBTWTags(str, tag);
        if (text.isEmpty()) {
            return defaultValue;
        }
        try {
            return (T) valueType.parse(text.get());
        } catch (Exception exception) {
            log.error("获取标签中的文件属性时出现异常，原因：无法将文本转换成 {}", valueType.getType().getSimpleName(), exception);
        }
        return defaultValue;
    }


    /**
     * 将包含标签的字符串解析为标签和对应值的列表。
     * @param str 需要解析的字符串，可能包含形如<tag>value</tag>的标签
     * @return 解析后的标签和对应值的列表，
     * 如果包含标签则返回：tag::value
     * 如果字符串不包含标签，则返回包含"prefix::"前缀和原字符串的列表
     */
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
