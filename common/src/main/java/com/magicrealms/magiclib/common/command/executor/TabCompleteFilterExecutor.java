package com.magicrealms.magiclib.common.command.executor;


import com.magicrealms.magiclib.common.command.callback.CommandFailure;
import com.magicrealms.magiclib.common.command.enums.CommandRule;
import com.magicrealms.magiclib.common.command.enums.PermissionType;
import com.magicrealms.magiclib.common.command.factory.CommandFilterFactory;
import com.magicrealms.magiclib.common.command.factory.PermissionFilterFactory;
import com.magicrealms.magiclib.common.command.filter.label.LabelFilter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 具体过滤器工厂类
 * @date 2024-05-10
 */
public class TabCompleteFilterExecutor extends AbstractFilterExecutor {

    public final static TabCompleteFilterExecutor INSTANCE = new TabCompleteFilterExecutor();

    private TabCompleteFilterExecutor() {}

    @Override
    protected Boolean abstractFilter(@NotNull CommandSender sender,
                              @NotNull String label,
                              @NotNull String[] args,
                              @Nullable String text,
                              @Nullable String permission,
                              @Nullable String annotateLabel,
                              @Nullable CommandRule rule,
                              @Nullable PermissionType permissionType,
                              @Nullable AtomicBoolean executorFlg, @Nullable Consumer<CommandFailure> failureConsumer) {
        return CommandFilterFactory.INSTANCE.create(rule).filter(sender, label, args, text, permission, annotateLabel)
                && PermissionFilterFactory.INSTANCE.create(permissionType).filter(sender, label, args, text, permission, annotateLabel)
                && LabelFilter.INSTANCE.filter(sender, label, args, text, permission, annotateLabel);
    }
}
