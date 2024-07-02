package com.magicrealms.magiclib.common.manage;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 配置文件管理器
 * @date 2024-05-26
 **/
public class ConfigManage implements IConfigManage {

    private final MagicRealmsPlugin PLUGIN;

    private final Map<String, YamlConfiguration> ALL_CONFIG = new HashMap<>();

    public ConfigManage(MagicRealmsPlugin plugin) {
        this.PLUGIN = plugin;
    }

    @Override
    public void loadConfig(String... configPaths) {
        for (String path : configPaths) {
            getYamlConfiguration(path).ifPresent(yml -> ALL_CONFIG.put(path, yml));
        }
    }

    @Override
    public void reloadAllConfig() {
        for (String path: ALL_CONFIG.keySet()) {
            getYamlConfiguration(path).ifPresent(yml -> ALL_CONFIG.put(path, yml));
        }
    }

    @Override
    public void reloadConfig(@NotNull String name, @NotNull Consumer<Boolean> callBack) {
        Optional<String> path = ALL_CONFIG.keySet().stream().filter(p -> StringUtils.endsWithIgnoreCase(p, name)).findFirst();
        if (path.isEmpty()) {
            callBack.accept(false);
            return;
        }
        getYamlConfiguration(path.get()).ifPresent(yml -> ALL_CONFIG.put(path.get(), yml));
        callBack.accept(true);
    }

    @Override
    public String getYmlValue(@NotNull String path, @NotNull String key) {
        YamlConfiguration config = ALL_CONFIG.get(path);
        if (config == null) {
            return StringUtil.EMPTY;
        }
        if (config.contains(key)){
            return config.getString(key);
        }
        return createYmlKey(path, key).orElse(StringUtil.EMPTY);
    }

    @Override
    public int getYmlIntegerValue(@NotNull String path, @NotNull String key, int def) {
        String value = getYmlValue(path, key);
        try {
            return Integer.parseInt(value);
        } catch (Exception exception){
            PLUGIN.getLoggerManager().error("获取YML文件属性时出现异常，原因：无法将文本转换成 Integer，Key：" + key, exception);
        }
        return def;
    }

    @Override
    public long getYmlLongValue(@NotNull String path, @NotNull String key, long def) {
        String value = getYmlValue(path, key);
        try {
            return Long.parseLong(value);
        } catch (Exception exception){
            PLUGIN.getLoggerManager().error("获取YML文件属性时出现异常，原因：无法将文本转换成 Long，Key：" + key, exception);
        }
        return def;
    }

    @Override
    public float getYmlFloatValue(@NotNull String path, @NotNull String key, float def) {
        String value = getYmlValue(path, key);
        try {
            return Float.parseFloat(value);
        } catch (Exception exception){
            PLUGIN.getLoggerManager().error("获取YML文件属性时出现异常，原因：无法将文本转换成 Float，Key：" + key, exception);
        }
        return def;
    }

    @Override
    public double getYmlDoubleValue(@NotNull String path, @NotNull String key, double def) {
        String value = getYmlValue(path, key);
        try {
            return Double.parseDouble(value);
        } catch (Exception exception){
            PLUGIN.getLoggerManager().error("获取YML文件属性时出现异常，原因：无法将文本转换成 Double，Key：" + key, exception);
        }
        return def;
    }

    @Override
    public boolean getYmlBooleanValue(@NotNull String path, @NotNull String key) {
        return StringUtils.equalsIgnoreCase(getYmlValue(path, key), "true");
    }

    @Override
    public Optional<List<String>> getYmlListValue(@NotNull String path, @NotNull String key) {
        YamlConfiguration config = ALL_CONFIG.get(path);
        if (config == null) {
            return Optional.empty();
        }
        if (config.contains(key) && config.isList(key)) {
            return Optional.of(config.getStringList(key));
        }
        return createYmlListKey(path, key);
    }

    public Optional<Set<String>> getYmlSubKeys(@NotNull String path, @NotNull String key, boolean deep) {
        YamlConfiguration config = ALL_CONFIG.get(path);
        if (config == null) {
            return Optional.empty();
        }
        Optional<ConfigurationSection> configurationSection = Optional.ofNullable(config.getConfigurationSection(key));
        if (configurationSection.isPresent()) {
            return Optional.of(configurationSection.get().getKeys(deep));
        }
        if (!config.contains(key)) {
            return createConfigurationSection(path, key, deep);
        }
        return Optional.empty();
    }



    @Override
    public boolean containsYmlKey(@NotNull String path, @NotNull String key) {
        YamlConfiguration config = ALL_CONFIG.get(path);
        return config != null && config.contains(key);
    }



    private Optional<YamlConfiguration> getYamlConfiguration(@NotNull String path){
        String fileName = path + ".yml";
        File config = new File(PLUGIN.getDataFolder(), fileName);
        if (config.exists()) {
            return Optional.of(YamlConfiguration.loadConfiguration(config));
        }
        PLUGIN.getLoggerManager().info("正在生成 " + fileName + "文件...");
        long timeMillis = System.currentTimeMillis();
        try {
            PLUGIN.saveResource(fileName, false);
            PLUGIN.getLoggerManager().info(fileName + " 文件生成完毕，耗时 " + (System.currentTimeMillis() - timeMillis) + " ms");
            return Optional.of(YamlConfiguration.loadConfiguration(new File(PLUGIN.getDataFolder(), fileName)));
        } catch (Exception exception) {
            PLUGIN.getLoggerManager().error(fileName + " 文件生成失败", exception);
        }
        return Optional.empty();
    }

    private Optional<Set<String>> createConfigurationSection(@NotNull String path, @NotNull String key, boolean deep) {
        YamlConfiguration config = ALL_CONFIG.get(path);
        InputStream inputStream = PLUGIN.getResource(path + ".yml");
        if (config == null || inputStream == null) {
            return Optional.empty();
        }
        try(Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            if (!defConfig.contains(key)) {
                return Optional.empty();
            }
            config.set(key, defConfig.get(key));
            config.save(new File(PLUGIN.getDataFolder(), path + ".yml"));
            Optional<ConfigurationSection> configurationSection = Optional.ofNullable(config.getConfigurationSection(key));
            if (configurationSection.isPresent()) {
                return Optional.of(configurationSection.get().getKeys(deep));
            }
        } catch (IOException e) {
            PLUGIN.getLoggerManager().error("创建YML文件Key时出现未知的异常", e);
        }
        return Optional.empty();

    }

    private Optional<String> createYmlKey(@NotNull String path, @NotNull String key) {
        YamlConfiguration config = ALL_CONFIG.get(path);
        InputStream inputStream = PLUGIN.getResource(path + ".yml");
        if (config == null || inputStream == null) {
            return Optional.empty();
        }
        try(Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            if (!defConfig.contains(key)) {
                return Optional.empty();
            }
            config.set(key, defConfig.get(key));
            config.save(new File(PLUGIN.getDataFolder(), path + ".yml"));
            if (defConfig.isString(key) || defConfig.isBoolean(key) || defConfig.isDouble(key) || defConfig.isInt(key)
                || defConfig.isLong(key)
            ) {
                return Optional.ofNullable(config.getString(key));
            }
        } catch (IOException e) {
            PLUGIN.getLoggerManager().error("创建YML文件Key时出现未知的异常", e);
        }
        return Optional.empty();
    }

    private Optional<List<String>> createYmlListKey(@NotNull String path, @NotNull String key) {
        YamlConfiguration config = ALL_CONFIG.get(path);
        InputStream inputStream = PLUGIN.getResource(path + ".yml");
        if (config == null || inputStream == null) {
            return Optional.empty();
        }
        try(Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
            if (!defConfig.contains(key)) {
                return Optional.empty();
            }
            config.set(key, defConfig.get(key));
            config.save(new File(PLUGIN.getDataFolder(), path + ".yml"));
            if (defConfig.isList(key)) {
                return Optional.of(config.getStringList(key));
            }
        } catch (IOException e) {
            PLUGIN.getLoggerManager().error("创建YML文件Key时出现未知的异常", e);
        }
        return Optional.empty();
    }



}
