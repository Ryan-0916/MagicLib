package com.magicrealms.magiclib.common.store;



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
    boolean exists(String key);
    boolean setValue(String key, String value, long expire);
    boolean setObject(String key, Object value, long expire);
    boolean lSetValue(String key, long expire, String... values);
    boolean lSetValue(String key, int maxSize, String... value);
    boolean lSetObject(String key, long expire, Object... values);
    boolean lSetValue(String key, int maxSize, Object... values);
    boolean rSetValue(String key, long expire, String... values);
    boolean rSetValue(String key, int maxSize, String... value);
    boolean rSetObject(String key, long expire, Object... values);
    boolean rSetValue(String key, int maxSize, Object... values);
    boolean hSetValue(String key, LinkedHashMap<String, String> values, long expire);
    boolean hSetValue(String key, String subKey, String value, long expire);
    boolean hSetObject(String key, String subKey, Object value, long expire);
    boolean hSetObject(String key, LinkedHashMap<String, Object> values, long expire);
    Optional<String> getValue(String key);
    <T> Optional<T> getObject(String key, Class<T> clazz);
    Optional<String> hGetValue(String key, String subKey);
    <T> Optional<T> hGetObject(String key, String subKey,  Class<T> clazz);
    Optional<List<String>> hGetAllValue(String key);
    <T> Optional<List<T>> hGetAllObject(String key,  Class<T> clazz);
    Optional<List<String>> getAllValue(String key);
    <T> Optional<List<T>> getAllObject(String key, Class<T> clazz);
    boolean removeKey(String... key);
    boolean removeKeyByPrefix(String... prefix);
    boolean removeHkey(String key, String... subKey);
    void publishValue(String channel, String value);
    boolean tryLock(String lockKey, String lockHolder, long expire);
    boolean releasedLock(String lockKey, String lockHolder);
}
