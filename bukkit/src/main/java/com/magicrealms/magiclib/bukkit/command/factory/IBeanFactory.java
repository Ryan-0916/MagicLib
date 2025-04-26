package com.magicrealms.magiclib.bukkit.command.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Ryan-0916
 * @date 2023-10-01
 */
@SuppressWarnings("unused")
public interface IBeanFactory {

    /**
     * 通过 Bean Name 获取携带 @Component 注解的 JavaBean
     * @param name Java bean 的 Simple name
     * @return 返回该 Java bean
     */
    <T> T getBean(String name);

    /**
     * 通过注解注解的所有 JavaBean
     * @param annotationType 注解
     * @return 返回所有 Java bean
     */
    <T> Map<String, T> getBeansWithAnnotation(Class<? extends Annotation> annotationType);
}
