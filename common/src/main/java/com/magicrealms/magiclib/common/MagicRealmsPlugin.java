package com.magicrealms.magiclib.common;


import com.magicrealms.magiclib.common.manage.CommandManager;
import com.magicrealms.magiclib.common.manage.ConfigManager;
import com.magicrealms.magiclib.common.manage.LoggerManager;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * @author Ryan-0916
 * @Desc 抽象插件类
 * @date 2024-05-26
 */
@Getter
@SuppressWarnings("unused")
public abstract class MagicRealmsPlugin extends JavaPlugin {

    private ConfigManager configManager;
    private CommandManager commandManager;
    private LoggerManager loggerManager;

    public MagicRealmsPlugin() {

    }

    @Override
    public void onEnable() {
        this.setupLoggerManager();
        this.setupConfigManager();
        this.setupCommandManger();
    }

    private void setupConfigManager() {
        this.configManager = new ConfigManager(this);
    }

    private void setupCommandManger() {
        this.commandManager = new CommandManager(this);
    }

    private void setupLoggerManager() {
        this.loggerManager = new LoggerManager(this);
    }

    public void dependenciesCheck(Runnable runnable, String... dependenciesName) {
        for (String name : dependenciesName) {
            if (Bukkit.getPluginManager().getPlugin(name) == null) {
                loggerManager.warning("缺少必要依赖项 " + name);
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        runnable.run();
    }

    public boolean dependenciesCheck(String... dependenciesName) {
        for (String name : dependenciesName) {
            if (Bukkit.getPluginManager().getPlugin(name) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成资源文件
     * @param resourcePath 资源文件生成路径
     * @param referencePath 参照文件资源路径，如果参照文件资源路径为空，则将默认为资源文件生成路径
     * @param replace 如果资源文件已经存在是否将其替换
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveResource(String resourcePath, @Nullable String referencePath, boolean replace) {
        if (StringUtils.isBlank(resourcePath)) {
            throw new IllegalArgumentException("资源文件生成路径为空");
        }
        referencePath = (referencePath != null ? referencePath : resourcePath).replace('\\', '/');
        InputStream in = super.getResource(referencePath);
        if (in == null) {
            throw new IllegalArgumentException("参照路径或者资源路径的文件找不到，导致没有生成模板参考");
        }
        File outFile = new File(super.getDataFolder(), resourcePath);
        File outDir = new File(super.getDataFolder(), resourcePath.substring(0,
                Math.max(resourcePath.lastIndexOf('/'), 0)));
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        if (outFile.exists() && !replace) {
            getLoggerManager().warning("不可以将" + outFile.getName() + "保存至" + outFile + "原因" + outFile.getName() + "已经存在");
            return;
        }

        try(OutputStream out = new FileOutputStream(outFile)){
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
        } catch (IOException exception) {
            getLoggerManager().error("不可以将" + outFile.getName() + "保存至" + outFile, exception);
        }
    }

    protected abstract void loadConfig(ConfigManager configManager);

    protected abstract void registerCommand(CommandManager commandManager);

}
