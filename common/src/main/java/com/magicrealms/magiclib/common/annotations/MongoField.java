package com.magicrealms.magiclib.common.annotations;

import com.magicrealms.magiclib.common.adapt.DefaultFieldAdapter;
import com.magicrealms.magiclib.common.adapt.FieldAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author Ryan-0916
 * @Desc 此注解用于对象与 MongoDB 文档之间的自动转换：
 * - 序列化：将对象转换为 MongoDB 文档时，MongoField 注解提供了字段如何映射到文档中的指示。
 * - 反序列化：从 MongoDB 文档中读取数据时，MongoField 注解帮助将文档字段映射回对象字段。
 * @date 2025-05-01
 */
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时仍然可用
@Target(ElementType.FIELD) // 注解作用于类字段
public @interface MongoField {

    /**
     * 自定义字段在 MongoDB 中的字段名称。
     * 默认情况下，该字段将采用下划线命名法，使用 Java 对象名称将驼峰式命名转换为下划线命名
     * 如果希望 MongoDB 中字段名称与 Java 字段名称不一致，可以通过此属性进行自定义。
     * @return 自定义字段名称，默认为空字符串
     */
    String name() default "";

    boolean id() default false;

    /**
     * 是否忽略该字段。
     * 如果设置为 true，则该字段在 MongoDB 文档中会被忽略，既不进行序列化，也不进行反序列化。
     * 默认为 false，表示字段会参与序列化和反序列化过程。
     * @return 是否忽略该字段，默认为 false
     */
    boolean ignore() default false;

    /**
     * 是否递归处理该字段。
     * 如果设置为 true，则字段值将会递归地处理其嵌套对象，该对象将不会被处理，但其内部对象会被处理
     * 默认为 false，表示不递归处理。
     * @return 是否递归处理字段，默认为 false
     */
    boolean recursive() default false;

    /**
     * 指定自定义的字段适配器，用于对字段的序列化和反序列化过程进行定制。
     * 适配器可以自定义字段的转换逻辑（如对复杂类型的处理）。默认适配器是 {@link DefaultFieldAdapter}。
     * @return 自定义字段适配器类，默认为 DefaultFieldAdapter.class
     */
    Class<? extends FieldAdapter> adapter() default DefaultFieldAdapter.class;
}