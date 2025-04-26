package com.magicrealms.magiclib.bukkit.command.factory;
import com.magicrealms.magiclib.bukkit.command.enums.CommandRule;
import com.magicrealms.magiclib.bukkit.command.filter.IChannelFilter;
import com.magicrealms.magiclib.bukkit.command.filter.command.CaseInsensitiveRegexCommandFilter;
import com.magicrealms.magiclib.bukkit.command.filter.command.RegexCommandFilter;
import com.magicrealms.magiclib.common.exception.UnknownTypeException;

/**
 * @author Ryan-0916
 * @Desc 指令过滤工厂
 * @date 2023-10-01
 */
public class CommandFilterFactory implements IFilterFactory {

    public final static CommandFilterFactory INSTANCE = new CommandFilterFactory();

    private CommandFilterFactory() {}

    /**
     * 获取过滤器
     * @param e 对应 @Command、@TabComplete 注解中 commander 值
     * @return 返回相应的过滤器
     */
    @Override
    public IChannelFilter create(Enum<?> e) {
        if (e instanceof CommandRule type) {
            return switch (type) {
                case REGEX -> RegexCommandFilter.INSTANCE;
                case CASE_INSENSITIVE_REGEX -> CaseInsensitiveRegexCommandFilter.INSTANCE;
            };
        }
        throw new UnknownTypeException("指令过滤工厂无法解析未知的枚举类型");
    }
}
