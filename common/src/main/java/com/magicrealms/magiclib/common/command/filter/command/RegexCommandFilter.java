package com.magicrealms.magiclib.common.command.filter.command;

import com.magicrealms.magiclib.common.command.filter.AbstractChannelFilter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 正则校验-指令过滤器
 * @date 2023-10-01
 **/
public class RegexCommandFilter extends AbstractChannelFilter {

    public static final RegexCommandFilter INSTANCE = new RegexCommandFilter();
    private RegexCommandFilter(){}

    @Override
    public Boolean filter(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return text != null && Pattern.compile(text).matcher(StringUtils.join(args, " ")).find();
    }
}
