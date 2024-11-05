package com.magicrealms.magiclib.common.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * @author Ryan-0916
 * @Desc 枚举类，用于定义不同类型的解析器。
 * 每个枚举常量都代表一种数据类型，并提供了一个将字符串解析为该数据类型的函数。
 * @date 2024-10-30
 */
public enum ParseType {

    /**
     * 字符串类型解析器
     * 使用 {@link String#valueOf(Object)} 方法将值转换为字符串。
     */
    STRING(String::valueOf, String.class),

    /**
     * 整数类型解析器，
     * 使用 {@link Integer#parseInt(String)} 方法将字符串解析为整数。
     */
    INTEGER(Integer::parseInt, Integer.class),

    /**
     * 长整数类型解析器，
     * 使用 {@link Long#parseLong(String)} 方法将字符串解析为长整数。
     */
    LONG(Long::parseLong, Long.class),

    /**
     * 浮点数类型解析器，
     * 使用 {@link Float#parseFloat(String)} 方法将字符串解析为浮点数。
     */
    FLOAT(Float::parseFloat, Float.class),

    /**
     * 双精度浮点数类型解析器，
     * 使用 {@link Double#parseDouble(String)} 方法将字符串解析为双精度浮点数。
     */
    DOUBLE(Double::parseDouble, Double.class),

    /**
     * 布尔类型解析器，使用自定义函数将字符串解析为布尔值。
     * 当字符串为 "true"（忽略大小写）时，返回 {@code true}；否则返回 {@code false}。
     * 注意：这个解析器与标准的 Java 解析方式不同，它接受任何非 "false" 的字符串为 {@code true}。
     * 为了严格匹配，请考虑使用 {@link StringUtils#equalsIgnoreCase(CharSequence, CharSequence)}
     * StringUtils.equalsIgnoreCase(value, "true") 的替代实现。
     */
    BOOLEAN(value -> StringUtils.equalsIgnoreCase(value, "true"), Boolean.class);

    /* 解析函数用于将字符串转换为对应的数据类型对象。*/
    private final Function<String, Object> parser;

    /* 数据类型的 Class 对象，用于表示解析后的结果类型。 */
    @Getter
    private final Class<?> type;

    /**
     * 构造方法，初始化解析函数和数据类型。
     * @param parser 解析函数
     * @param type 数据类型
     */
    ParseType(Function<String, Object> parser, Class<?> type) {
        this.parser = parser;
        this.type = type;
    }

    /**
     * 使用解析函数将字符串解析为对应的数据类型对象。
     * @param value 要解析的字符串
     * @return 解析后的对象
     */
    public Object parse(String value) {
        return parser.apply(value);
    }
}
