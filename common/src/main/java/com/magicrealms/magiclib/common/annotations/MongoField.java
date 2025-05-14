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
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MongoField {

    String name() default "";

    FieldId id() default @FieldId;

    boolean ignore() default false;

    boolean recursive() default false;

    Class<? extends FieldAdapter> adapter() default DefaultFieldAdapter.class;

}