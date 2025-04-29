package com.magicrealms.magiclib.common.utils;

import com.magicrealms.magiclib.common.exception.DistributedLockException;
import com.magicrealms.magiclib.common.store.RedisStore;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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
     * 同步执行任务（带分布式锁）
     * @param store Redis存储接口
     * @param lockKey 锁的key
     * @param holder 锁持有者标识（建议使用UUID或线程ID）
     * @param expire 锁过期时间（毫秒）
     * @param task 要执行的任务
     * @param <T> 返回类型
     * @return 任务执行结果
     * @throws DistributedLockException 获取锁失败或执行异常
     */
    public static <T> T doWithLock(RedisStore store,
                                   String lockKey,
                                   String holder,
                                   long expire,
                                   Supplier<T> task) throws DistributedLockException {
        try {
            if (!store.tryLock(lockKey, holder, expire)) {
                throw new DistributedLockException("获取分布式锁失败，lockKey: " + lockKey);
            }
            return task.get();
        } catch (Exception e) {
            throw new DistributedLockException("执行任务时发生异常", e);
        } finally {
            store.releaseLock(lockKey, holder);
        }
    }

    /**
     * 异步执行任务（带分布式锁），会持续等待直到获取锁
     * @param store Redis存储接口
     * @param lockKey 锁的key
     * @param holder 锁持有者标识
     * @param expire 锁过期时间（毫秒）
     * @param task 要执行的任务
     * @return CompletableFuture 异步结果
     */
    @SuppressWarnings("BusyWait")
    public static CompletableFuture<Void> doAsyncWithLock(RedisStore store,
                                                                     String lockKey,
                                                                     String holder,
                                                                     long expire,
                                                                     Runnable task) {
        return CompletableFuture.runAsync(() -> {
            try {
                while (true) {
                    try {
                        if (store.tryLock(lockKey, holder, expire)) { break; }
                        Thread.sleep(WAIT_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw e;
                    }
                }
                task.run();
            } catch (InterruptedException e) {
                log.warn("异步任务被中断", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("异步执行任务时发生异常", e);
            } finally {
                store.releaseLock(lockKey, holder);
            }
        });
    }
}
