package com.magicrealms.magiclib.bukkit.manage;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.enums.ParseType;
import com.magicrealms.magiclib.bukkit.manage.entity.FileMirrorInfo;
import com.magicrealms.magiclib.common.utils.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 配置文件管理器
 * @date 2024-05-26
 */
@SuppressWarnings("unused")
public class ConfigManager {

    private final MagicRealmsPlugin PLUGIN;

    /* 所有以及在缓存中加载的文件 */
    private final Map<String, FileMirrorInfo> ALL_CONFIG = new HashMap<>();

    public ConfigManager(MagicRealmsPlugin plugin) {
        this.PLUGIN = plugin;
    }

    /**
     * 加载指定的多个资源配置文件，并将其存储到内存中。
     *
     * @param resourcesPath 可变参数，表示需要加载的资源文件路径列表
     *                      说明：
     *                      - 遍历传入的资源文件路径列表。
     *                      - 调用 {@link ConfigManager#getYamlConfiguration(String)} 方法获取每个资源文件的 Yaml 配置对象。
     *                      - 如果配置对象存在，将资源路径与 {@link FileMirrorInfo} 对象存入 ALL_CONFIG。
     */
    public void loadConfig(String... resourcesPath) {
        Arrays.stream(resourcesPath).forEach(resourcePath ->
                getYamlConfiguration(resourcePath).ifPresent(e ->
                        ALL_CONFIG.put(resourcePath, new FileMirrorInfo(resourcePath, resourcePath, e))
                )
        );
    }

    /**
     * 加载镜像配置文件并存储到内存中。
     *
     * @param mirrorPath 镜像配置文件的路径，不能为空
     * @param sourcePath 源配置文件的路径，不能为空，用于生成镜像配置文件
     *                   <p>
     *                   说明：
     *                   - 当文件夹中存在此文件时，我们将直接返回文件的配置对象，弱文件夹中不存在此文件时我们将以 sourcePath 资源为模板生成文件
     *                   - 调用 {@link ConfigManager#getYamlConfiguration(String)} 方法获取每个资源文件的 Yaml 配置对象。
     *                   - 如果配置对象存在，将资源路径与 {@link FileMirrorInfo} 对象存入 ALL_CONFIG。
     */
    public void loadMirrorConfig(String mirrorPath, @Nullable String sourcePath) {
        getYamlConfiguration(mirrorPath, sourcePath)
                .ifPresent(e -> ALL_CONFIG.put(mirrorPath, new FileMirrorInfo(mirrorPath, sourcePath, e)));
    }

    /**
     * 获取指定路径的 Yaml 配置对象。
     *
     * @param resourcePath 资源文件文件的路径，不能为空
     * @return Optional<YamlConfiguration> 如果文件存在，则返回 YAML 配置对象；否则返回空的 Optional
     */
    private Optional<YamlConfiguration> getYamlConfiguration(String resourcePath) {
        return getYamlConfiguration(resourcePath, null);
    }

    /**
     * 获取指定路径的 Yaml 配置对象。
     * 如果文件不存在，则基于参考路径生成文件。
     *
     * @param mirrorPath 镜像文件路径，不能为空
     * @param sourcePath 参考路径（可选），用于在生成配置文件时作为模板
     *                   如果 sourcePath 为空，将由 mirrorPath 资源文件作为模板生成配置文件
     * @return Optional<YamlConfiguration> 如果文件存在或成功生成，则返回 YAML 配置对象；否则返回空的 Optional
     */
    private Optional<YamlConfiguration> getYamlConfiguration(String mirrorPath, @Nullable String sourcePath) {
        mirrorPath = ensureYmlExtension(mirrorPath);
        sourcePath = sourcePath != null ? ensureYmlExtension(sourcePath) : null;

        /* 获取配置文件 */
        File config = new File(PLUGIN.getDataFolder(), mirrorPath);

        /* 文件已存在，直接加载 */
        if (config.exists()) {
            return Optional.of(YamlConfiguration.loadConfiguration(config));
        }

        /* 文件不存在，尝试生成文件 */
        PLUGIN.getLoggerManager().info("正在生成 " + mirrorPath + " 文件...");
        long timeMillis = System.currentTimeMillis();
        try {
            PLUGIN.saveResource(mirrorPath, sourcePath, false);
            PLUGIN.getLoggerManager().info(mirrorPath + " 文件生成完毕，耗时 " + (System.currentTimeMillis() - timeMillis) + " ms");
            return Optional.of(YamlConfiguration.loadConfiguration(new File(PLUGIN.getDataFolder(), mirrorPath)));
        } catch (Exception exception) {
            PLUGIN.getLoggerManager().error(mirrorPath + " 文件生成失败", exception);
        }
        return Optional.empty();
    }

    /**
     * 确保给定的路径文件的格式
     *
     * @param path 文件路径字符串
     * @return 处理后的路径字符串，确保以 ".yml" 结尾
     */
    private String ensureYmlExtension(String path) {
        return path.endsWith(".yml") ? path : path + ".yml";
    }

    /**
     * 重新加载所有文件池中的文件
     * 此方法会重新获取文件池中所有资源文件，请谨慎使用
     * 推荐使用单文件重新加载 {@link ConfigManager#reloadConfig(String, Consumer)}
     */
    public void reloadAllConfig() {
        ALL_CONFIG.values().forEach(fileMirrorInfo ->
                getYamlConfiguration(fileMirrorInfo.getMirrorPath(), fileMirrorInfo.getSourcePath()).ifPresent(
                        fileMirrorInfo::setYamlConfiguration));
    }

    /**
     * 重新加载除特定文件之外中的全部文件
     * 此方法会重新获取文件池中除特定文件之外所有资源文件，请谨慎使用
     * 推荐使用单文件重新加载 {@link ConfigManager#reloadConfig(String, Consumer)}
     */
    public void reloadConfig(String... ignoreMirrorPath) {
        ALL_CONFIG.values().stream().filter(e -> Arrays.stream(ignoreMirrorPath)
                .noneMatch(p -> StringUtils.equals(p, e.getMirrorPath())))
                .forEach(fileMirrorInfo ->
                        getYamlConfiguration(fileMirrorInfo.getMirrorPath(), fileMirrorInfo.getSourcePath()).ifPresent(
                            fileMirrorInfo::setYamlConfiguration)
                );
    }

    /**
     * 重新加载单个配置文件
     * @param mirrorPath 镜像文件地址，既文件真实地址，而源库文件地址
     * @param callBack 回调函数，用于返回操作结果（成功或失败）
     */
    public void reloadConfig(String mirrorPath, Consumer<Boolean> callBack) {
        Optional<FileMirrorInfo> fileMirrorInfoOptional = Optional.ofNullable(ALL_CONFIG.get(mirrorPath));
        if (fileMirrorInfoOptional.isEmpty()) {
            callBack.accept(false);
            return;
        }
        FileMirrorInfo fileMirrorInfo = fileMirrorInfoOptional.get();
        getYamlConfiguration(fileMirrorInfo.getMirrorPath(), fileMirrorInfo.getSourcePath())
                .ifPresent(fileMirrorInfo::setYamlConfiguration);
        callBack.accept(true);
    }

    /**
     * 获取配置文件中的值
     * 如果找不到该配置文件则返回 StringUtil.EMPTY
     * 如果在配置文件中找不到该 key 则会从资源文件中镜像生成到配置文件中
     * 如若资源文件找不到该 key 则返回 StringUtil.EMPTY
     * @param mirrorPath 配置文件路径
     * @param key 配置文件中的键下级键请用.衔接
     * @return 配置文件值
     */
    @Subst("")
    public String getYmlValue(String mirrorPath, String key) {
        Optional<FileMirrorInfo> fileMirrorInfo = Optional.ofNullable(ALL_CONFIG.get(mirrorPath));
        return fileMirrorInfo.isPresent() ? fileMirrorInfo.get()
                .getYamlConfiguration().contains(key) ? fileMirrorInfo.get()
                .getYamlConfiguration().getString(key) : generateMirrorYamlKey(mirrorPath, key).orElse(StringUtil.EMPTY) : StringUtil.EMPTY;
    }

    /**
     * 判断配置文件中是否存在该 key
     * @param mirrorPath 配置文件真实地址
     * @param key 配置文件 key
     * @return 返回配置文件中是否存在该 key
     */
    public boolean containsYmlKey(String mirrorPath, String key) {
        Optional<FileMirrorInfo> fileMirrorInfo = Optional.ofNullable(ALL_CONFIG.get(mirrorPath));
        return fileMirrorInfo.isPresent() &&
                fileMirrorInfo.get().getYamlConfiguration().contains(key);
    }

    /**
     * 获取配置文件中的值
     * @param mirrorPath 配置文件路径
     * @param key 配置文件中的键下级键请用.衔接
     * @param defaultValue 默认值，如果配置文件中找不到该 key
     * 或该 key 的值与我们所需的值类型不匹配时将返回该值
     * @param valueType {@link ParseType} 转换类型
     */
    @SuppressWarnings("unchecked")
    public <T> T getYmlValue(String mirrorPath, String key, T defaultValue, ParseType valueType) {
        String value = getYmlValue(mirrorPath, key);
        try {
            return (T) valueType.parse(value);
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    /**
     * 镜像生成 Yaml key，并返回新的 Yaml 对象
     * @param mirrorPath 镜像文件地址，我们将在该文件中生成 key
     * @param key 需要镜像生成的 key
     * @return 返回生成该 key 的 value （如若此 key 非父 key）
     */
    private Optional<YamlConfiguration> mirrorCreateYamlKey(String mirrorPath, String key) {
        Optional<FileMirrorInfo> fileMirrorInfo = Optional.ofNullable(ALL_CONFIG.get(mirrorPath));
        if (fileMirrorInfo.isEmpty()) {
            return Optional.empty();
        }

        /* 获取插件静态资源下的源文件的路径 */
        Optional<InputStream> sourceFileInputStream = Optional.ofNullable(PLUGIN.getResource(ensureYmlExtension(fileMirrorInfo.get().getSourcePath())));
        if (sourceFileInputStream.isEmpty()) {
            return Optional.empty();
        }

        try (Reader reader = new InputStreamReader(sourceFileInputStream.get(),
                StandardCharsets.UTF_8)) {
            YamlConfiguration sourceYamlConfig = YamlConfiguration.loadConfiguration(reader);
            /* 如若参照文件中未找到该 Key */
            if (!sourceYamlConfig.contains(key)) {
                return Optional.empty();
            }
            YamlConfiguration mirrorYamlConfig = fileMirrorInfo.get().getYamlConfiguration();
            mirrorYamlConfig.set(key, sourceYamlConfig.get(key));
            mirrorYamlConfig.save(new File(PLUGIN.getDataFolder(), ensureYmlExtension(mirrorPath)));
            return Optional.of(mirrorYamlConfig);
        } catch (IOException e) {
            PLUGIN.getLoggerManager().error("镜像生成 YAML 文件 Key 时出现未知的异常", e);
        }
        return Optional.empty();
    }

    /**
     * 镜像生成 Yaml key，并返回新的 key 的值 （非 List 类型）
     * @param mirrorPath 镜像文件地址，我们将在该文件中生成 key
     * @param key 需要镜像生成的 key
     * @return 返回生成该 key 的 value （如若此 key 非父 key）
     */
    private Optional<String> generateMirrorYamlKey(String mirrorPath, String key) {
        Optional<YamlConfiguration> yamlConfiguration = mirrorCreateYamlKey(mirrorPath, key);
        return yamlConfiguration.isPresent()
                && !yamlConfiguration.get().isList(key)
                ? Optional.ofNullable(yamlConfiguration.get().getString(key)) : Optional.empty();
    }

    /**
     * 镜像生成 Yaml key，并返回新的 key 的值 （List 类型）
     * @param mirrorPath 镜像文件地址，我们将在该文件中生成 key
     * @param key 需要镜像生成的 key
     * @return 返回生成该 key 的 value （如若此 key 非父 key）
     */
    private Optional<List<String>> generateMirrorYamlListKey(String mirrorPath, String key) {
        Optional<YamlConfiguration> yamlConfiguration = mirrorCreateYamlKey(mirrorPath, key);
        return yamlConfiguration.isPresent()
                && yamlConfiguration.get().isList(key)
                ? Optional.of(yamlConfiguration.get().getStringList(key)) : Optional.empty();
    }

    /**
     * 获取指定镜像文件中 key 对应的列表值，如果在缓存中找不到，则尝试生成镜像文件并获取值。
     *
     * @param mirrorPath 镜像文件的路径，用于查找对应的配置。
     * @param key 需要查找的 key，返回该 key 对应的列表值。
     * @return 如果 key 存在且其值为列表类型，返回一个包含字符串的列表的 Optional，否则返回 Optional.empty()。
     *         如果缓存中找不到该 key 的值，尝试生成镜像文件并查找。
     */
    public Optional<List<String>> getYmlListValue(String mirrorPath, String key) {
        Optional<FileMirrorInfo> fileMirrorInfo = Optional.ofNullable(ALL_CONFIG.get(mirrorPath));
        return fileMirrorInfo.isPresent() ? fileMirrorInfo.get()
                .getYamlConfiguration().contains(key) && fileMirrorInfo.get()
                .getYamlConfiguration().isList(key) ? Optional.of(fileMirrorInfo.get()
                .getYamlConfiguration().getStringList(key)) : generateMirrorYamlListKey(mirrorPath, key) : Optional.empty();
    }

    public Optional<Set<String>> getYmlSubKeys(String mirrorPath, String key, boolean deep) {
        return Optional.ofNullable(ALL_CONFIG.get(mirrorPath))
                .flatMap(mirrorInfo -> Optional.ofNullable(mirrorInfo.getYamlConfiguration().getConfigurationSection(key))
                .map(section -> section.getKeys(deep))
                .or(() -> generateMirrorYamlConfigurationSection(mirrorPath, key, deep)));

    }

    private Optional<Set<String>> generateMirrorYamlConfigurationSection(String mirrorPath, String key, boolean deep) {
        Optional<YamlConfiguration> yamlConfiguration
                = mirrorCreateYamlKey(mirrorPath, key);
        return yamlConfiguration.map(section -> section.getKeys(deep))
                        .or(Optional::empty);
    }

}
