package com.magicrealms.magiclib.common.utils;

import com.magicrealms.magiclib.common.store.RedisStore;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author Ryan-0916
 * @Desc 分布式锁相关处理
 * @date 2025-04-29
 */
@Slf4j
@SuppressWarnings("unused")
public final class RedissonUtil {

    private static final int WAIT_MS = 100;

    private RedissonUtil() {}

    /**
     * 同步执行无返回值任务（带分布式锁）
     * @param store Redis存储接口
     * @param lockKey 锁的key
     * @param holder 锁持有者标识
     * @param expire 锁过期时间（毫秒）
     * @param task 要执行的任务
     * @throws Exception 任务执行异常
     */
    public static void doWithLock(
            RedisStore store,
            String lockKey,
            String holder,
            long expire,
            Runnable task
    ) throws Exception {
        doWithLock(store, lockKey, holder, expire, () -> {
            task.run();
            return null;
        });
    }

    /**
     * 同步执行带返回值任务（带分布式锁）
     * @param store Redis存储接口
     * @param lockKey 锁的key
     * @param holder 锁持有者标识
     * @param expire 锁过期时间（毫秒）
     * @param task 要执行的任务（带返回值）
     * @param <T> 返回值类型
     * @return 任务执行结果
     * @throws Exception 任务执行异常
     */
    @SuppressWarnings("BusyWait")
    public static <T> T doWithLock(
            RedisStore store,
            String lockKey,
            String holder,
            long expire,
            Callable<T> task
    ) throws Exception {
        try {
            while (true) {
                try {
                    if (store.tryLock(lockKey, holder, expire)) break;
                    Thread.sleep(WAIT_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new InterruptedException("获取锁时被中断");
                }
            }
            return task.call();
        } finally {
            store.releaseLock(lockKey, holder);
        }
    }

    /**
     * 异步执行无返回值任务（带分布式锁）
     * @param store Redis存储接口
     * @param lockKey 锁的key
     * @param holder 锁持有者标识
     * @param expire 锁过期时间（毫秒）
     * @param task 要执行的任务
     * @return CompletableFuture<Void>
     */
    public static CompletableFuture<Void> doAsyncWithLock(
            RedisStore store,
            String lockKey,
            String holder,
            long expire,
            Runnable task
    ) {
        return CompletableFuture.runAsync(() -> {
            try {
                doWithLock(store, lockKey, holder, expire, task);
            } catch (Exception e) {
                throw new CompletionException("异步加锁任务执行失败", e);
            }
        });
    }

    /**
     * 异步执行带返回值任务（带分布式锁）
     * @param store Redis存储接口
     * @param lockKey 锁的key
     * @param holder 锁持有者标识
     * @param expire 锁过期时间（毫秒）
     * @param task 要执行的任务（带返回值）
     * @param <T> 返回值类型
     * @return CompletableFuture<T>
     */
    public static <T> CompletableFuture<T> doWithLockAsync(
            RedisStore store,
            String lockKey,
            String holder,
            long expire,
            Callable<T> task
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return doWithLock(store, lockKey, holder, expire, task);
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

}
