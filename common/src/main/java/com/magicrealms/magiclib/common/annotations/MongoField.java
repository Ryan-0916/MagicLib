package com.magicrealms.magiclib.common.annotations;

import com.magicrealms.magiclib.common.converter.DefaultConverter;
import com.magicrealms.magiclib.common.converter.FieldConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MongoField {
    String name() default "";
    boolean ignore() default false;
    boolean recursive() default false;
    Class<? extends FieldConverter> converter() default DefaultConverter.class;
}
