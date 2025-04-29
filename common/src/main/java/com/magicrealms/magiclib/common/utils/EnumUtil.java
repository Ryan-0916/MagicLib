package com.magicrealms.magiclib.common.utils;

import java.util.*;

/**
 * @author Ryan-0916
 * @Desc 枚举处理工具类
 * @date 2024-10-30
 */
public final class EnumUtil {

    private EnumUtil() {}
    
    /**
     * 根据名称匹配枚举项（不区分大小写）
     * @param <T> 枚举类型泛型参数
     * @param enumClass 枚举类对象，不能为null
     * @param name 要匹配的枚举名称，null或空字符串将返回Optional.empty()
     * @return 包含匹配枚举项的Optional对象，无匹配时返回empty
     */
    public static <T extends Enum<T>> Optional<T> getMatchingEnum(Class<T> enumClass, String name) {
        if (enumClass == null || name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    /**
     * 批量匹配多个枚举名称（不区分大小写）
     * @param <T> 枚举类型泛型参数
     * @param enumClass 枚举类对象，不能为null
     * @param names 要匹配的枚举名称数组，null或空数组将返回空Set
     * @return 包含所有匹配枚举项的不可变Set集合，无匹配时返回空Set
     * @see #getMatchingEnum(Class, String) 单名称匹配方法
     */
    public static <T extends Enum<T>> Set<T> getAllMatchingEnum(Class<T> enumClass, String... names) {
        if (enumClass == null || names == null || names.length == 0) {
            return Collections.emptySet();
        }
        Set<T> result = EnumSet.noneOf(enumClass);
        for (String name : names) {
            if (name != null && !name.trim().isEmpty()) {
                getMatchingEnum(enumClass, name.trim()).ifPresent(result::add);
            }
        }
        return Collections.unmodifiableSet(result);
    }
}