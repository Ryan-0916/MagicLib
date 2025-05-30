package com.magicrealms.magiclib.bukkit.command.annotations;


import com.magicrealms.magiclib.bukkit.annotations.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ryan-0916
 * @Desc 插件注册阶段将反射携带此注解的类
 * 并对携带此注解类中携带 @Command，@TabComplete 注解的方法进行注册
 * @date 2023-10-01
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandListener {

}
