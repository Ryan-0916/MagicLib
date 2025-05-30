package com.magicrealms.magiclib.bukkit;


import com.github.retrooper.packetevents.PacketEvents;


import com.magicrealms.magiclib.bukkit.manage.CommandManager;
import com.magicrealms.magiclib.bukkit.manage.ConfigManager;
import com.magicrealms.magiclib.bukkit.manage.LoggerManager;
import com.magicrealms.magiclib.bukkit.manage.PacketManager;
import com.magicrealms.magiclib.bukkit.processor.AppContext;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
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

@SuppressWarnings("unused")
public abstract class MagicRealmsPlugin extends JavaPlugin {

    @Getter
    protected ConfigManager configManager;
    @Getter
    protected LoggerManager loggerManager;
    protected CommandManager commandManager;
    protected PacketManager packetManager;

    private final AppContext APP_CONTEXT;

    public MagicRealmsPlugin() {
        APP_CONTEXT =  new AppContext(this.getClass().getPackage().getName(),
                this.getClass().getClassLoader());
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        this.setupLoggerManager();
        this.setupConfigManager();
        this.setupCommandManger();
        this.setupPacketManager();
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        loggerManager.shutdown();
    }

    private void setupConfigManager() {
        this.configManager = new ConfigManager(this);
    }

    private void setupCommandManger() {
        this.commandManager = new CommandManager(this, APP_CONTEXT);
    }

    private void setupLoggerManager() {
        this.loggerManager = new LoggerManager(this);
    }

    private void setupPacketManager() { this.packetManager = new PacketManager(this, APP_CONTEXT); }

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
        InputStream in = getResource(referencePath);
        if (in == null) {
            throw new IllegalArgumentException("参照路径或者资源路径的文件找不到，导致没有生成模板参考");
        }
        File outFile = new File(getDataFolder(), resourcePath);
        File outDir = new File(getDataFolder(), resourcePath.substring(0,
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

    protected abstract void registerPacketListener(PacketManager packetManager);
}
