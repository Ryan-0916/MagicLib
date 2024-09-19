package com.magicrealms.magiclib.common.manage;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ryan-0916
 * @Desc 日志管理器
 * @date 2024-05-26
 */
@SuppressWarnings("unused")
@Slf4j
public class LoggerManage {

   private final MagicRealmsPlugin PLUGIN;

    public LoggerManage(MagicRealmsPlugin plugin) {
        this.PLUGIN = plugin;
    }

    public void info(String msg) {
        log.info(msg);
    }

    public void warning(String msg) {
        log.warn(msg);
    }

    public void error(String msg, Throwable throwable) {
        log.error(msg, throwable);
    }

}
