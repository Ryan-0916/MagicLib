package com.magicrealms.magiclib.bukkit.manage;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.bukkit.command.annotations.Command;
import com.magicrealms.magiclib.bukkit.command.annotations.TabComplete;
import com.magicrealms.magiclib.bukkit.command.enums.CommandFailureCause;
import com.magicrealms.magiclib.bukkit.command.executor.CommandFilterExecutor;
import com.magicrealms.magiclib.bukkit.command.executor.TabCompleteFilterExecutor;
import com.magicrealms.magiclib.bukkit.command.records.CommandFailure;
import com.magicrealms.magiclib.bukkit.processor.AppContext;
import org.bukkit.Bukkit;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 指令管理器
 * @date 2024-05-25
 */
@SuppressWarnings("unused")
public class CommandManager {

    private final MagicRealmsPlugin PLUGIN;
    private final AppContext APP_CONTEXT;

    public CommandManager(MagicRealmsPlugin plugin, AppContext appContext) {
        this.PLUGIN = plugin;
        this.APP_CONTEXT = appContext;
    }

    public void registerCommand(String name, Consumer<CommandFailure> failure) {
        Optional.ofNullable(Bukkit.getPluginCommand(name)).ifPresent(
                commandManager -> {
                    /* 注册执行器 */
                    commandManager.setExecutor((sender, command, label, args) -> {
                        AtomicBoolean executorFlg = new AtomicBoolean(false);
                        APP_CONTEXT.getCommandMethods().forEach((k, v) -> Arrays.stream(k.getAnnotations()).filter(this::isCommandAnnotation).forEach(annotation -> {
                            if (CommandFilterExecutor.INSTANCE.filter(sender, label, args, annotation, executorFlg, failure)) {
                                try {
                                    k.setAccessible(true);
                                    k.invoke(v, sender, args);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    PLUGIN.getLoggerManager().error("插件 " + PLUGIN.getName() + " 反射指令时出现未知异常", e);
                                }
                            }
                        }));
                        if (!executorFlg.get()) {
                            failure.accept(new CommandFailure(sender, CommandFailureCause.UN_KNOWN_COMMAND));
                        }
                        return true;
                    });

                    /* 注册补全器 */
                    commandManager.setTabCompleter((sender, command, label, args) -> {
                        List<String> tabCompleteList = new ArrayList<>();
                        APP_CONTEXT.getCommandMethods().forEach((k, v) -> Arrays.stream(k.getAnnotations()).filter(this::isTabCompleteAnnotation).forEach(annotation -> {
                            if (TabCompleteFilterExecutor.INSTANCE.filter(sender, label, args, annotation, null, null)) {
                                try {
                                    k.setAccessible(true);
                                    Object obj = k.invoke(v, sender, args);
                                    if (obj instanceof Collection<?> objs) {
                                        for (Object o : objs) {
                                            tabCompleteList.add((String) o);
                                        }
                                    }
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    PLUGIN.getLoggerManager().error("插件 " + PLUGIN.getName() + " 反射指令补全器时出现未知异常", e);
                                }
                            }
                        }));
                        return tabCompleteList;
                    });

                }
        );
    }

    public void unregisterCommandFeatures() {
    }

    /**
     * 判断注解是否是 @Command 注解
     * @param annotation 注解
     * @return 返回 是否
     */
    private boolean isCommandAnnotation(Annotation annotation) { return annotation instanceof Command; }

    /**
     * 判断注解是否是 @TabComplete 注解
     * @param annotation 注解
     * @return 返回 是否
     */
    private boolean isTabCompleteAnnotation(Annotation annotation) {
        return annotation instanceof TabComplete;
    }
}
