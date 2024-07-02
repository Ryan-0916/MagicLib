package com.magicrealms.magiclib.common.command.executor;

import com.magicrealms.magiclib.common.command.callback.CommandFailure;
import com.magicrealms.magiclib.common.command.enums.CommandRule;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * @author Ryan-0916
 * @Desc 抽象过滤器
 * @date 2024-05-10
 **/
public abstract class AbstractFilterExecutor {

    public Boolean filter(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Annotation annotation,
                          @Nullable AtomicBoolean executorFlg, @Nullable Consumer<CommandFailure> failureConsumer) {
        Class<? extends Annotation> aClass = annotation.annotationType();
        CommandRule rule = null;
        String text = null, permission = null, annotateLabel = null;
        PermissionType permissionType = null;
        try {
            rule = (CommandRule) aClass.getDeclaredMethod("rule").invoke(annotation);
            text = (String) aClass.getDeclaredMethod("text").invoke(annotation);
            permissionType = (PermissionType) aClass.getDeclaredMethod("permissionType").invoke(annotation);
            permission = (String) aClass.getDeclaredMethod("permission").invoke(annotation);
            annotateLabel = (String) aClass.getDeclaredMethod("label").invoke(annotation);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Bukkit.getLogger().log(Level.WARNING, "指令过滤器获取注解属性时出现未知问题 - 位置 com.magicrealms.core.executor.CommandFilterExecutor");
        }

        return abstractFilter(sender, label, args, text, permission, annotateLabel, rule, permissionType, executorFlg, failureConsumer);
    }

    protected abstract Boolean abstractFilter(@NotNull CommandSender sender,
                                       @NotNull String label,
                                       @NotNull String[] args,
                                       @Nullable String text,
                                       @Nullable String permission,
                                       @Nullable String annotateLabel,
                                       @Nullable CommandRule rule,
                                       @Nullable PermissionType permissionType,
                                       @Nullable AtomicBoolean executorFlg, @Nullable Consumer<CommandFailure> failureConsumer);
}
