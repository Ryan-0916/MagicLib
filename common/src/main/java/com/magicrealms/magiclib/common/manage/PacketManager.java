package com.magicrealms.magiclib.common.manage;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.packet.annotations.Receive;
import com.magicrealms.magiclib.common.packet.annotations.Send;
import com.magicrealms.magiclib.common.processor.AppContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;


/**
 * @author Ryan-0916
 * @Desc 数据包管理器，负责注册和处理数据包监听方法
 * @date 2025-04-10
 */
@SuppressWarnings("unused")
@Slf4j
public class PacketManager {

    private final MagicRealmsPlugin PLUGIN;
    /* 存储接收和发送数据包的监听方法，按优先级、类型进行分类 */
    private final Map<ListenerPriority, Map<PacketType, Map<Method, Object>>> receiveMethods;
    private final Map<ListenerPriority, Map<PacketType, Map<Method, Object>>> sendMethods;

    public PacketManager(MagicRealmsPlugin plugin, AppContext appContext) {
        this.PLUGIN = plugin;
        this.receiveMethods = new HashMap<>();
        this.sendMethods = new HashMap<>();
        /* 遍历所有带有 @PacketListener 注解的方法，进行分类存储 */
        appContext.getPacketMethods().forEach(this::processPacketMethod);
    }

    /**
     * 注册数据包监听器
     * 这个方法用于将接收和发送的数据包监听器添加到 ProtocolLibrary
     */
    public void registerListeners() {
        Optional.ofNullable(ProtocolLibrary.getProtocolManager()).ifPresent(protocolManager -> Stream.concat(
                receiveMethods.keySet().stream(),
                sendMethods.keySet().stream()
        ).distinct().forEach(priority -> {
            PacketType[] types = getPacketTypesForPriority(priority);
            protocolManager.addPacketListener(createPacketAdapter(priority, types));
        }));
    }

    /**
     * 获取指定优先级的数据包类型
     * @param priority 优先级
     * @return 包含接收和发送数据包类型的数组
     */
    private PacketType[] getPacketTypesForPriority(ListenerPriority priority) {
        return Stream.concat(
                        getClassesFromPriorityMap(receiveMethods, priority),
                        getClassesFromPriorityMap(sendMethods, priority)
                ).distinct().toArray(PacketType[]::new);
    }

    /**
     * 创建 PacketAdapter 监听器
     * @param priority 优先级
     * @param types 数据包类型
     * @return PacketAdapter 实例
     */
    private PacketAdapter createPacketAdapter(ListenerPriority priority, PacketType[] types) {
        return new PacketAdapter(PLUGIN, priority, types) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                invokeMethodsForEvent(event, receiveMethods);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                invokeMethodsForEvent(event, sendMethods);
            }

            /**
             * 通用的方法，用于调用对应优先级的接收或发送方法
             * @param event 数据包事件
             * @param methodsMap 方法映射
             */
            private void invokeMethodsForEvent(PacketEvent event,
                                               Map<ListenerPriority,
                                                       Map<PacketType, Map<Method, Object>>> methodsMap) {
                if (methodsMap.containsKey(priority)) {
                    Map<PacketType, Map<Method, Object>> classMethodsMap = methodsMap.get(priority);
                    if (classMethodsMap.containsKey(event.getPacketType())) {
                        Map<Method, Object> methods = classMethodsMap.get(event.getPacketType());
                        methods.forEach((method, instance) -> {
                            try {
                                method.setAccessible(true);
                                method.invoke(instance, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                PLUGIN.getLoggerManager().error("插件 " + PLUGIN.getName() + " 反射数据包监听器时出现未知异常", e);
                            }
                        });
                    }
                }
            }
        };
    }

    /**
     * 处理带有 @PacketListener 注解的方法，分类到接收或发送方法中
     * @param method  被扫描的方法
     * @param object  方法所在的类实例
     */
    private void processPacketMethod(Method method, Object object) {
        Arrays.stream(method.getAnnotations())
                .filter(this::isReceiveAnnotation)
                .forEach(annotation -> processPacketAnnotation(annotation, method, object, true));

        Arrays.stream(method.getAnnotations())
                .filter(this::isSendAnnotation)
                .forEach(annotation -> processPacketAnnotation(annotation, method, object, false));
    }

    /**
     * 处理数据包注解，将方法分类到接收或发送方法映射中
     * @param annotation 注解实例
     * @param method     被注解的方法
     * @param object     方法所在的类实例
     * @param isReceive  是否为接收注解（true：接收，false：发送）
     */
    private void processPacketAnnotation(Annotation annotation, Method method, Object object, boolean isReceive) {
        try {
            PacketType.Protocol protocol = (PacketType.Protocol) annotation.annotationType().getDeclaredMethod("protocol").invoke(annotation);
            PacketType.Sender sender = (PacketType.Sender) annotation.annotationType().getDeclaredMethod("sender").invoke(annotation);
            int packetId = (int) annotation.annotationType().getDeclaredMethod("packetId").invoke(annotation);
            ListenerPriority priority = (ListenerPriority) annotation.annotationType().getDeclaredMethod("priority").invoke(annotation);
            PacketType type = PacketType.findCurrent(protocol, sender, packetId);
            if (isReceive) {
                storePacketMethod(receiveMethods, priority, type, method, object);
            } else {
                storePacketMethod(sendMethods, priority, type, method, object);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("初始化数据包注解时出现错误，方法：{}，错误：{}", method.getName(), e.getMessage(), e);
        }
    }

    /**
     * 将数据包方法存储到相应的优先级和类型映射中
     * @param methods    存储方法的映射（接收或发送）
     * @param priority   数据包监听优先级
     * @param type       数据包类型
     * @param method     被注册的方法
     * @param object     方法所在的类实例
     */
    private void storePacketMethod(Map<ListenerPriority, Map<PacketType, Map<Method, Object>>> methods,
                                   ListenerPriority priority, PacketType type, Method method, Object object) {
        methods.computeIfAbsent(priority, k -> new HashMap<>())
                .computeIfAbsent(type, k -> new HashMap<>())
                .put(method, object);
    }

    /**
     * 检查注解是否为接收数据包的注解
     * @param annotation 注解实例
     * @return 是否为接收注解
     */
    private boolean isReceiveAnnotation(Annotation annotation) {
        return annotation instanceof Receive;
    }

    /**
     * 检查注解是否为发送数据包的注解
     * @param annotation 注解实例
     * @return 是否为发送注解
     */
    private boolean isSendAnnotation(Annotation annotation) {
        return annotation instanceof Send;
    }

    private Stream<PacketType> getClassesFromPriorityMap(Map<ListenerPriority, Map<PacketType, Map<Method, Object>>> map, ListenerPriority priority) {
        return Optional.ofNullable(map.get(priority))
                .stream()
                .flatMap(p -> p.keySet().stream());
    }
}

