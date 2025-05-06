package com.magicrealms.magiclib.common.adapt;

/**
 * @author Ryan-0916
 * @Desc MongoDB 自定义字段转换器
 * 该抽象类提供了对 MongoDB 字段的自定义转换功能。它允许开发者通过实现此类，
 * 自定义对象字段的序列化和反序列化过程。例如，你可以为特定的字段类型（如日期、复杂对象等）
 * 定义专门的转换规则，以确保在 MongoDB 存储和读取时符合特定的格式或要求。
 * 此类包含两个抽象方法：
 * - `write()`：用于将字段从 Java 对象转换为 MongoDB 存储格式。
 * - `read()`：用于将 MongoDB 存储格式转换为 Java 对象字段。
 * - T: 表示字段在 Java 对象中的类型。
 * - U: 表示该字段在 MongoDB 中存储的类型。
 * @date 2025-05-01
 */
public abstract class FieldAdapter<T, U> {

    /**
     * 将字段从 Java 对象转换为 MongoDB 存储格式
     * 该方法需要实现具体的转换逻辑，将 Java 对象中的字段（类型为 T）转换为
     * MongoDB 存储时所需要的类型（类型为 U）。
     * @param writer Java 对象中的字段（类型为 T）
     * @return 转换后的 MongoDB 存储格式（类型为 U）
     */
    public abstract U write(T writer);

    /**
     * 将 MongoDB 存储格式转换为 Java 对象字段
     * 该方法需要实现具体的转换逻辑，将 MongoDB 存储的字段（类型为 U）转换为
     * Java 对象中的字段（类型为 T）。
     * @param reader MongoDB 存储格式中的字段（类型为 U）
     * @return 转换后的 Java 对象字段（类型为 T）
     */
    public abstract T read(U reader);
}