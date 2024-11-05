package com.magicrealms.magiclib.common.manage;

import com.magicrealms.magiclib.common.enums.ParseType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 配置文件管理器
 * @date 2024-05-26
 */
@SuppressWarnings("unused")
public interface IConfigManage {
    void loadConfig(String... filePaths);
    void reloadAllConfig();
    void reloadConfig(@NotNull String path, @NotNull Consumer<Boolean> callBack);
    String getYmlValue(@NotNull String path, @NotNull String key);
    <T> T getYmlValue(@NotNull String path, @NotNull String key, T defaultValue, @NotNull ParseType valueType);
    Optional<List<String>> getYmlListValue(@NotNull String path, @NotNull String key);
    Optional<Set<String>> getYmlSubKeys(@NotNull String path, @NotNull String key, boolean f);
    boolean containsYmlKey(@NotNull String path, @NotNull String key);
}
