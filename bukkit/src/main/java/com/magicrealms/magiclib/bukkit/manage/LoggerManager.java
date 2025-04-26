package com.magicrealms.magiclib.bukkit.manage;

import com.magicrealms.magiclib.bukkit.MagicRealmsPlugin;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * @author Ryan-0916
 * @Desc 日志管理器
 * @date 2024-05-26
 */
@SuppressWarnings("unused")
public class LoggerManager {
    private final MagicRealmsPlugin plugin;
    private final Logger bukkitLogger;
    private FileHandler infoFileHandler;
    private FileHandler errorFileHandler;
    private ConsoleHandler consoleHandler;  // 新增控制台处理器
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    @Getter
    private String currentLogDirectory;

    public LoggerManager(MagicRealmsPlugin plugin) {
        this.plugin = plugin;
        this.bukkitLogger = plugin.getLogger();
        initialize();
    }

    private void initialize() {
        try {
            setupLogDirectory();
            setupFileHandlers();
            setupConsoleHandler();  // 新增控制台处理器设置
            addHandlersToLogger();
        } catch (IOException e) {
            handleInitializationError(e);
        }
    }

    private void setupLogDirectory() {
        currentLogDirectory = DATE_FORMAT.format(new Date());
        File logsDir = new File(plugin.getDataFolder(), "Logs/" + currentLogDirectory);
        if (!logsDir.exists() && !logsDir.mkdirs()) {
            throw new IllegalStateException("无法创建日志目录: " + logsDir.getAbsolutePath());
        }
    }

    private void setupFileHandlers() throws IOException {
        String basePath = plugin.getDataFolder() + "/Logs/" + currentLogDirectory + "/";

        /* 配置info日志处理器 */
        infoFileHandler = new FileHandler(basePath + "info.log", true);
        infoFileHandler.setFormatter(createLogFormatter());
        infoFileHandler.setLevel(Level.INFO);
        infoFileHandler.setFilter(record -> record.getLevel().intValue() <= Level.INFO.intValue());

        /* 配置error日志处理器 */
        errorFileHandler = new FileHandler(basePath + "error.log", true);
        errorFileHandler.setFormatter(createLogFormatter());
        errorFileHandler.setLevel(Level.WARNING);
    }

    // 新增方法：配置控制台处理器
    private void setupConsoleHandler() {
        consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(createLogFormatter());
        consoleHandler.setLevel(Level.WARNING);  // 只处理WARNING及以上级别的日志
    }

    private Formatter createLogFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("[%s] [%s] - %s%n",
                        TIME_FORMAT.format(new Date(record.getMillis())),
                        record.getLevel().getName(),
                        record.getMessage());
            }
        };
    }

    private void addHandlersToLogger() {
        for (Handler handler : bukkitLogger.getHandlers()) {
            bukkitLogger.removeHandler(handler);
        }
        bukkitLogger.addHandler(infoFileHandler);
        bukkitLogger.addHandler(errorFileHandler);
        bukkitLogger.addHandler(consoleHandler);
        bukkitLogger.setUseParentHandlers(false);
    }

    private void handleInitializationError(Exception e) {
        bukkitLogger.severe("日志系统初始化失败:");
        bukkitLogger.log(Level.SEVERE, e.getMessage(), e);
        bukkitLogger.warning("将使用控制台日志作为后备方案");
    }

    // 其余方法保持不变...
    public void info(String msg) {
        log(Level.INFO, msg);
    }

    public void info(String format, Object... args) {
        log(Level.INFO, String.format(format, args));
    }

    public void warning(String msg) {
        log(Level.WARNING, msg);
    }

    public void warning(String format, Object... args) {
        log(Level.WARNING, String.format(format, args));
    }

    public void error(String msg) {
        log(Level.SEVERE, msg);
    }

    public void error(String format, Object... args) {
        log(Level.SEVERE, String.format(format, args));
    }

    public void error(String msg, Throwable throwable) {
        bukkitLogger.log(Level.SEVERE, msg, throwable);
    }

    private void log(Level level, String msg) {
        bukkitLogger.log(level, msg);
    }

    public void shutdown() {
        closeHandler(infoFileHandler);
        closeHandler(errorFileHandler);
        closeHandler(consoleHandler);
    }

    private void closeHandler(Handler handler) {
        if (handler != null) {
            try {
                handler.close();
            } catch (Exception e) {
                bukkitLogger.warning("关闭日志处理器时出错: " + e.getMessage());
            }
        }
    }
}