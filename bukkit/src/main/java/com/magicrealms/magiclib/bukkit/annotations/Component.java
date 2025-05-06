package com.magicrealms.magiclib.bukkit.annotations;

import java.lang.annotation.*;

/**
 * @author Ryan-0916
 * @Desc 插件注册阶段将反射携带此注解的类
 * 从而实现动态 Command 与 Packet 的注册
 * @date 2023-10-01
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Component { }
