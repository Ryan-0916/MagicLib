package com.magicrealms.magiclib.bukkit.command.executor;


import com.magicrealms.magiclib.bukkit.command.records.CommandFailure;
import com.magicrealms.magiclib.bukkit.command.enums.CommandFailureCause;
import com.magicrealms.magiclib.bukkit.command.enums.CommandRule;
import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.bukkit.command.factory.CommandFilterFactory;
import com.magicrealms.magiclib.bukkit.command.factory.PermissionFilterFactory;
import com.magicrealms.magiclib.bukkit.command.filter.label.LabelFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.magicrealms.magiclib.bukkit.command.enums.CommandFailureCause.PERMISSION_DENIED;


/**
 * @author Ryan-0916
 * @Desc 此处用于过滤命令
 * @date 2024-05-10
 */
public class CommandFilterExecutor extends AbstractFilterExecutor {

    public final static CommandFilterExecutor INSTANCE = new CommandFilterExecutor();

    private CommandFilterExecutor() {}

    @Override
    protected Boolean abstractFilter(CommandSender sender,
                              String label,
                              String[] args,
                              @Nullable String text,
                              @Nullable String permission,
                              @Nullable String annotateLabel,
                              @Nullable CommandRule rule,
                              @Nullable PermissionType permissionType,
                              @Nullable AtomicBoolean executorFlg, @Nullable Consumer<CommandFailure> failureConsumer) {
        boolean commanderFlg = CommandFilterFactory.INSTANCE.create(rule).
                filter(sender, label, args, text, permission, annotateLabel)
                && LabelFilter.INSTANCE.filter(sender, label, args, text, permission, annotateLabel);
        boolean permissionFlg = PermissionFilterFactory.INSTANCE.create(permissionType).filter(sender, label, args, text, permission, annotateLabel);

        /* 判断是否匹配到了指令 */
        if (commanderFlg) {

            /* 告诉调用者，我们匹配到了指令 */
            if (executorFlg != null) {
                executorFlg.set(true);
            }

            /* 因角色不匹配或权限不足时我们需要告诉执行者 */
            if (!permissionFlg && permissionType != null && failureConsumer != null) {
                failureConsumer.accept(switch (permissionType) {
                    case PLAYER -> new CommandFailure(sender, CommandFailureCause.NOT_PLAYER);
                    case ADMIN -> sender.isOp() ? new CommandFailure(sender, CommandFailureCause.NOT_PLAYER) :
                        new CommandFailure(sender, PERMISSION_DENIED);
                    case CONSOLE -> new CommandFailure(sender, CommandFailureCause.NOT_CONSOLE);
                    case PERMISSION -> sender instanceof Player ? new CommandFailure(sender, PERMISSION_DENIED) :
                            new CommandFailure(sender, CommandFailureCause.NOT_PLAYER);
                    default -> new CommandFailure(sender, PERMISSION_DENIED);
                });
            }
        }
        return commanderFlg && permissionFlg;
    }
}
