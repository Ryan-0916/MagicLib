package com.magicrealms.magiclib.common.command.processor;

import com.magicrealms.magiclib.common.command.annotations.Component;
import com.magicrealms.magiclib.common.command.annotations.Listener;
import com.magicrealms.magiclib.common.command.factory.IBeanFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Ryan-0916
 * @Desc 用于处理反射某包下所有包含 @Component 的注解
 * @date 2023-10-01
 */
@SuppressWarnings("unchecked")
@Slf4j
public class AppContext implements IBeanFactory {

    private final HashMap<String, Object> context = new HashMap<>();

    @Getter
    private final HashMap<Method, Object> methodHashMap = new HashMap<>();

    /**
     * 反射
     * @param packageName 包名
     * @param classLoader 类加载器
     */
    public AppContext(String packageName, ClassLoader classLoader) {
        try {
            ClassScanner scanner = new ClassScanner(packageName, classLoader, true, s -> true, s -> true);
            Set<Class<?>> classes = scanner.scanWithAnnotation(Component.class);
            classes.forEach(e -> {
                try {
                    if (!e.isAnnotation() && !e.isEnum() && !e.isInterface()) {
                        Constructor<Object> constructor = (Constructor<Object>) e.getConstructor();
                        constructor.setAccessible(true);
                        context.put(e.getSimpleName(), constructor.newInstance());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException ex) {
                    log.error("反射类时出现未知异常", ex);
                }
            });
            getBeansWithAnnotation(Listener.class).forEach(this::registerMethods);
        } catch (ClassNotFoundException | IOException e) {
            log.error("反射类时出现未知异常", e);
            System.exit(-1);
        }
    }


    /**
     * 通过 Bean Name 获取携带 @Component 注解的 JavaBean
     * @param name Java bean 的 Simple name
     * @return 返回该 Java bean
     */
    @Override
    public <T> T getBean(String name) {
        return (T) context.get(name);
    }

    /**
     * 获取包下所有携带该注解的类
     * @param annotationType 注解类
     * @return 返回所有 Java bean
     */
    @Override
    public <T> Map<String, T> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        HashMap<String, T> beans = new HashMap<>();
        context.forEach((k, v) -> {
            if (v.getClass().getAnnotation(annotationType) != null) {
                beans.put(k, (T) v);
            }
        });
        return beans;
    }

    /**
     * 注册某类下所有的方法
     * @param v 类名
     */
    private void registerMethods(String k, Object v) {
        for (Method method : v.getClass().getDeclaredMethods()) {
            methodHashMap.put(method, v);
        }
    }
}