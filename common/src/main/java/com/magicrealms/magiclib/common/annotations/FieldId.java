package com.magicrealms.magiclib.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ryan-0916
 * @Desc 表示 MongoDB 中的表 ID
 * @date 2025-05-14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldId {
    boolean enable() default false;
    boolean ignoreCase() default false;
}
