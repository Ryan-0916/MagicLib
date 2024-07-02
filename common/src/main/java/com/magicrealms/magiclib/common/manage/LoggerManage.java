package com.magicrealms.magiclib.common.manage;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Ryan-0916
 * @Desc 日志管理器
 * @date 2024-05-26
 **/
public class LoggerManage {

   private final MagicRealmsPlugin PLUGIN;

    public LoggerManage(MagicRealmsPlugin plugin) {
        this.PLUGIN = plugin;
    }

    public void info(String msg) {
        PLUGIN.getLogger().log(new LogRecord(Level.INFO, msg));
    }

    public void warning(String msg) {
        PLUGIN.getLogger().log(new LogRecord(Level.WARNING, msg));
    }
    public void error(String msg, Exception exception) {
        PLUGIN.getLogger().log(new LogRecord(Level.WARNING, msg));
        exception.printStackTrace();
    }

}
