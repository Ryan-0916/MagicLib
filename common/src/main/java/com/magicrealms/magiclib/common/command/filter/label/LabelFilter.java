package com.magicrealms.magiclib.common.command.filter.label;

import com.magicrealms.magiclib.common.command.filter.AbstractChannelFilter;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * @author Ryan-0916
 * @Desc 标签过滤器
 * @date 2023-10-01
 **/
public class LabelFilter extends AbstractChannelFilter {

    public static LabelFilter INSTANCE = new LabelFilter();

    private LabelFilter(){}

    @Override
    public Boolean filter(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args,
                          @Nullable String text, @Nullable String permission, @Nullable String annotateLabel) {
        return annotateLabel == null || annotateLabel.isEmpty() || Pattern.compile(annotateLabel, Pattern.CASE_INSENSITIVE).matcher(label).find();
    }
}
