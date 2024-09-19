package com.magicrealms.magiclib.common.store;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author Ryan-0916
 * @Desc Redis缓存相关操作
 * 以下操作皆采用短连接
 * @date 2024-05-30
 */
@SuppressWarnings("unused")
public interface IRedisStore {
    boolean exists(@NotNull String key);
    boolean setValue(@NotNull String key, @NotNull String value, long expire);
    boolean setObject(@NotNull String key, @NotNull Object value, long expire);
    boolean lSetValue(@NotNull String key, long expire, String... values);
    boolean lSetObject(@NotNull String key, long expire, Object... values);
    boolean rSetValue(@NotNull String key, long expire, String... values);
    boolean rSetObject(@NotNull String key, long expire, Object... values);
    boolean hSetValue(@NotNull String key, @NotNull LinkedHashMap<String, String> values, long expire);
    boolean hSetValue(@NotNull String key, @NotNull String subKey, @NotNull String value, long expire);
    boolean hSetObject(@NotNull String key, @NotNull String subKey, @NotNull Object value, long expire);
    boolean hSetObject(@NotNull String key, @NotNull LinkedHashMap<String, Object> values, long expire);
    Optional<String> getValue(@NotNull String key);
    <T> Optional<T> getObject(@NotNull String key, @NotNull Class<T> clazz);
    Optional<String> hGetValue(@NotNull String key, @NotNull String subKey);
    <T> Optional<T> hGetObject(@NotNull String key, @NotNull String subKey,  @NotNull Class<T> clazz);
    Optional<List<String>> hGetAllValue(@NotNull String key);
    <T> Optional<List<T>> hGetAllObject(@NotNull String key,  @NotNull Class<T> clazz);
    boolean removeKey(@NotNull String... key);
    boolean removeKeyByPrefix(@NotNull String... prefix);
    boolean removeHkey(@NotNull String key, @NotNull String... subKey);
    void publishValue(@NotNull String channel, @NotNull String value);
    boolean tryLock(@NotNull String lockKey, @NotNull String lockHolder, long expire);
    boolean releasedLock(@NotNull String lockKey, @NotNull String lockHolder);
}
