package com.magicrealms.magiclib.common.command.enums;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

/**
 * @author Ryan-0916
 * @Desc YML配置文件中不同类型值的不同转换方式
 * @date 2024-10-29
 */
public enum YmlValueType {
    STRING(String::valueOf, String.class),
    INTEGER(Integer::parseInt, Integer.class),
    LONG(Long::parseLong, Long.class),
    FLOAT(Float::parseFloat, Float.class),
    DOUBLE(Double::parseDouble, Double.class),
    BOOLEAN(value -> StringUtils.equalsIgnoreCase(value, "true"), Boolean.class);

    private final Function<String, Object> parser;

    @Getter
    private final Class<?> type;

    YmlValueType(Function<String, Object> parser, Class<?> type) {
        this.parser = parser;
        this.type = type;
    }

    public Object parse(String value) throws Exception {
        return parser.apply(value);
    }

}
