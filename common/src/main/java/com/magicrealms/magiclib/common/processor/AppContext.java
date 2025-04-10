package com.magicrealms.magiclib.common.processor;

import com.magicrealms.magiclib.common.annotations.Component;
import com.magicrealms.magiclib.common.command.annotations.CommandListener;
import com.magicrealms.magiclib.common.command.factory.IBeanFactory;
import com.magicrealms.magiclib.common.packet.annotations.PacketListener;
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
 * @Desc 处理反射某包下所有包含 @Component 注解的类，并注册 Command 和 Packet 方法
 * @date 2023-10-01
 */
@Slf4j
@SuppressWarnings("unchecked")
public class AppContext implements IBeanFactory {

    /* 用于存储所有 JavaBean 实例，key 为类的简单名称 */
    private final Map<String, Object> context = new HashMap<>();

    /* 存储所有带有 @CommandListener 注解的方法 */
    @Getter
    private final Map<Method, Object> commandMethods = new HashMap<>();

    /* 存储所有带有 @PacketListener 注解的方法 */
    @Getter
    private final Map<Method, Object> packetMethods = new HashMap<>();

    /**
     * 构造函数，反射加载指定包下的所有带有 @Component 注解的类
     * 同时注册带有 @CommandListener 和 @PacketListener 注解的方法
     * @param packageName 包名
     * @param classLoader 类加载器
     */
    public AppContext(String packageName, ClassLoader classLoader) {
        try {
            /* 使用 ClassScanner 扫描指定包下的所有类 */
            ClassScanner scanner = new ClassScanner(packageName, classLoader, true, s -> true, s -> true);
            Set<Class<?>> classes = scanner.scanWithAnnotation(Component.class);

            /* 遍历所有带有 @Component 注解的类 */
            classes.forEach(e -> {
                try {
                    if (!e.isAnnotation() && !e.isEnum() && !e.isInterface()) {
                        /* 获取类的无参构造器并实例化 */
                        Constructor<Object> constructor = (Constructor<Object>) e.getConstructor();
                        constructor.setAccessible(true);
                        /* 将实例化后的对象存入 context 中，key 为类的简单名称 */
                        context.put(e.getSimpleName(), constructor.newInstance());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException ex) {
                    log.error("反射类时出现未知异常：{}", e.getSimpleName(), ex);
                }
            });

            /* 注册带有 @CommandListener 注解的方法 */
            getBeansWithAnnotation(CommandListener.class).forEach(this::registerCommandMethods);
            /* 注册带有 @PacketListener 注解的方法 */
            getBeansWithAnnotation(PacketListener.class).forEach(this::registerPacketMethods);

        } catch (ClassNotFoundException | IOException e) {
            log.error("扫描类时出现异常", e);
            System.exit(-1); /* 程序启动失败，退出 */
        }
    }

    /**
     * 通过 Bean 名称获取 @Component 注解的 JavaBean 实例
     * @param name Java bean 的简单名称
     * @return 返回该 Java bean 实例
     */
    @Override
    public <T> T getBean(String name) {
        return (T) context.get(name);
    }

    /**
     * 获取所有带有指定注解的 JavaBean
     * @param annotationType 注解类
     * @return 返回所有带有指定注解的 JavaBean 实例
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
     * 注册带有 @CommandListener 注解的类中的方法
     * 将所有方法与其对应的类实例存入 commandMethods
     *
     * @param k 类的简单名称
     * @param v 类的实例
     */
    private void registerCommandMethods(String k, Object v) {
        for (Method method : v.getClass().getDeclaredMethods()) {
            commandMethods.put(method, v);
        }
    }

    /**
     * 注册带有 @PacketListener 注解的类中的方法
     * 将所有方法与其对应的类实例存入 packetMethods
     * @param k 类的简单名称
     * @param v 类的实例
     */
    private void registerPacketMethods(String k, Object v) {
        for (Method method : v.getClass().getDeclaredMethods()) {
            packetMethods.put(method, v);
        }
    }

}
