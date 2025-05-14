package com.magicrealms.magiclib.common.exception;

/**
 * @author Ryan-0916
 * @Desc 分布式锁被上锁时
 * @date 2025-04-29
 */
public class DistributedLockException extends Exception {

    public DistributedLockException(String message) {
        super(message);
    }

    public DistributedLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
