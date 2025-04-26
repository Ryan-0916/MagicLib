package com.magicrealms.magiclib.bukkit.command.factory;

import com.magicrealms.magiclib.bukkit.command.enums.PermissionType;
import com.magicrealms.magiclib.bukkit.command.filter.IChannelFilter;
import com.magicrealms.magiclib.bukkit.command.filter.permission.*;
import com.magicrealms.magiclib.common.exception.UnknownTypeException;

/**
 * @author Ryan-0916
 * @Desc 权限过滤工厂
 * @date 2023-10-01
 */
public class PermissionFilterFactory implements IFilterFactory {

    public final static PermissionFilterFactory INSTANCE = new PermissionFilterFactory();

    private PermissionFilterFactory() {}

    /**
     * 获取过滤器
     * @param e 对应 @Command、@TabComplete 注解中 permission 值
     * @return 返回相应的过滤器
     */
    @Override
    public IChannelFilter create(Enum<?> e) {
        if (e instanceof PermissionType type) {
            return switch (type) {
                case ALL -> AllPermissionFilter.INSTANCE;
                case PLAYER -> PlayerPermissionFilter.INSTANCE;
                case PERMISSION -> PermissionFilter.INSTANCE;
                case ADMIN -> AdminPermissionFilter.INSTANCE;
                case CONSOLE -> ConsolePermissionFilter.INSTANCE;
                case OP -> OpPermissionFilter.INSTANCE;
                case CONSOLE_OR_PERMISSION -> ConsoleOrPermissionFilter.INSTANCE;
            };
        }
        throw new UnknownTypeException("权限过滤工厂无法解析未知的枚举类型");
    }
}
