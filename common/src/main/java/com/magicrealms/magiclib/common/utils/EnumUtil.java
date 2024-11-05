package com.magicrealms.magiclib.common.utils;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Ryan-0916
 * @Desc 枚举工具类
 * @date 2024-10-30
 */
public final class EnumUtil {

    /**
     * 从枚举中匹配与给定名称一致的枚举项。
     * @param enumClass 枚举类，用于限定查找范围。
     * @param name 要匹配的枚举项名称，与枚举中的`name()`方法返回的值进行比较。
     * @return 返回一个包含匹配枚举项的`Optional`对象；如果没有找到匹配的枚举项，则返回一个空的`Optional`对象。
     * @param <T> 枚举类型的泛型参数，它必须是`Enum<T>`的一个子类型。
     */
    public static <T extends Enum<T>> Optional<T> getMatchingEnum(Class<T> enumClass, String name) {
        for (T constant : enumClass.getEnumConstants()) {
            if (constant.name().equals(name)) {
                return Optional.of(constant);
            }
        }
        return Optional.empty();
    }

    /**
     * 从指定的枚举类中查找与给定名称数组匹配的所有枚举项，并返回一个包含这些枚举项的集合。
     * @param enumClass 枚举类，用于限定查找范围。
     * @param names 要匹配的枚举项名称数组，与枚举中的 `name()` 方法返回的值进行比较。
     * @return 一个包含所有匹配枚举项的 `Set` 对象；如果没有找到匹配的枚举项，则返回一个空的 `Set` 对象。
     * @param <T> 枚举类型的泛型参数，它必须是 `Enum<T>` 的一个子类型。
     * @see #getMatchingEnum(Class, String) 该方法用于查找单个名称匹配的枚举项。
     */
    public static <T extends Enum<T>> Set<T> getAllMatchingEnum(Class<T> enumClass, String... names) {
        Set<T> set = EnumSet.noneOf(enumClass);
        for (final String name : names) {
            getMatchingEnum(enumClass, name).ifPresent(set::add);
        }
        return set;
    }

}
