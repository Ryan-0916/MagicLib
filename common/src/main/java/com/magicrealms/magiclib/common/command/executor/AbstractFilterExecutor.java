package com.magicrealms.magiclib.common.command.executor;

import com.magicrealms.magiclib.common.command.records.CommandFailure;
import com.magicrealms.magiclib.common.command.enums.CommandRule;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 采用策略模式，对命令/命令补全器采用不同的过滤方式
 * @date 2024-05-10
 */
@Slf4j
public abstract class AbstractFilterExecutor {

    public Boolean filter(CommandSender sender, String label, String[] args, Annotation annotation,
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
            log.error("指令过滤器获取注解属性时出现未知问题", e);
        }

        return abstractFilter(sender, label, args, text, permission, annotateLabel, rule, permissionType, executorFlg, failureConsumer);
    }

    protected abstract Boolean abstractFilter(CommandSender sender,
                                       String label,
                                       String[] args,
                                       @Nullable String text,
                                       @Nullable String permission,
                                       @Nullable String annotateLabel,
                                       @Nullable CommandRule rule,
                                       @Nullable PermissionType permissionType,
                                       @Nullable AtomicBoolean executorFlg, @Nullable Consumer<CommandFailure> failureConsumer);
}
