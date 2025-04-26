package com.magicrealms.magiclib.common.store;

import com.magicrealms.magiclib.common.MagicRealmsPlugin;
import com.magicrealms.magiclib.common.utils.GsonUtil;

import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ryan-0916
 * @Desc Redis缓存相关操作
 * 以下操作皆采用短连接
 * @date 2024-05-30
 */
@SuppressWarnings("unused")
public class RedisStore implements IRedisStore{

    private static final String SET_INHERITANCE_TTL_SCRIPT;
    private static final String LEFT_PUSH_SCRIPT;
    private static final String LEFT_PUSH_TRIM_SCRIPT;
    private static final String LEFT_PUSH_INHERITANCE_TTL_SCRIPT;
    private static final String RIGHT_PUSH_SCRIPT;
    private static final String RIGHT_PUSH_TRIM_SCRIPT;
    private static final String RIGHT_PUSH_INHERITANCE_TTL_SCRIPT;
    private static final String MAP_SET_SCRIPT;
    private static final String MAP_SET_INHERITANCE_TTL_SCRIPT;
    private static final String DISTRIBUTED_LOCK_SCRIPT;
    private static final String RELEASED_LOCK_SCRIPT;

    private final MagicRealmsPlugin PLUGIN;
    private final String HOST;
    private final int PORT;
    private final String PASSWORD;

    public RedisStore(MagicRealmsPlugin plugin, String host, int port, @Nullable String password) {
        this.PLUGIN = plugin;
        this.HOST = host;
        this.PORT = port;
        this.PASSWORD = password;
    }

    private Optional<Jedis> getConnection() {
        try {
            Jedis connection = new Jedis(HOST, PORT);
            if (PASSWORD != null) {
                connection.auth(PASSWORD);
            }
            return Optional.of(connection);
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 连接异常请检查 Redis 服务", e);
        }
        return Optional.empty();
    }

    /**
     * 通过脚本像队列中添加值
     * @param key Key
     * @param expire 有效期
     * expire > 0 时代表需要刷新该值的有效期，否则将继承原先 Key 的有效期
     * @param unInheritanceScript 无需继承有效期的 LUA 脚本
     * @param inheritanceScript 需要继承有效期的 LUA 脚本
     * @param values 值
     * @return 添加状态
     */
    private boolean pushValueByScript(String key, long expire, String unInheritanceScript, String inheritanceScript,
                                     String... values) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            if (expire > 0) connection.eval(unInheritanceScript, Collections.singletonList(key), Stream.concat(
                    Arrays.stream(values),
                    Stream.of(String.valueOf(expire))
            ).collect(Collectors.toList()));
            else connection.eval(inheritanceScript, Collections.singletonList(key), Arrays.asList(values));
            return true;
        } catch (Exception e) {
            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
        }
        return false;
    }

    private boolean pushValueSetMaxSizeByScript(String key, int maxSize, String script, String... values) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            connection.eval(script, Collections.singletonList(key), Stream.concat(
                    Arrays.stream(values),
                    Stream.of(String.valueOf(maxSize - 1))
            ).collect(Collectors.toList()));
            return true;
        } catch (Exception e) {
            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean exists(String key) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            return connection.exists(key);
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return false;
    }

    @Override
    public boolean setValue(String key, String value, long expire) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            if (expire > 0) connection.setex(key, expire, value);
            else connection.eval(SET_INHERITANCE_TTL_SCRIPT, Collections.singletonList(key), Collections.singletonList(value));
            return true;
        } catch (Exception e) {
            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean setObject(String key, Object value, long expire) {
        return setValue(key, GsonUtil.objectToJson(value), expire);
    }

    @Override
    public boolean lSetValue(String key, long expire, String... values) {
        return pushValueByScript(key, expire, LEFT_PUSH_SCRIPT, LEFT_PUSH_INHERITANCE_TTL_SCRIPT, values);
    }

    @Override
    public boolean lSetValue(String key, int maxSize, String... value) {
        if (maxSize < 0) {
            return lSetValue(key, -1, value);
        }
        return pushValueSetMaxSizeByScript(key, maxSize, LEFT_PUSH_TRIM_SCRIPT, value);
    }

    @Override
    public boolean lSetObject(String key, long expire, Object... values) {
        return lSetValue(key, expire, Arrays.stream(values).map(GsonUtil::objectToJson).collect(Collectors.joining()));
    }

    @Override
    public boolean lSetValue(String key, int maxSize, Object... values) {
        if (maxSize < 0) {
            return lSetValue(key, -1, Arrays.stream(values).map(GsonUtil::objectToJson).collect(Collectors.joining()));
        }
        return pushValueSetMaxSizeByScript(key, maxSize, LEFT_PUSH_TRIM_SCRIPT, Arrays.stream(values).map(GsonUtil::objectToJson).collect(Collectors.joining()));
    }

    @Override
    public boolean rSetValue(String key, long expire, String... values) {
        return pushValueByScript(key, expire, RIGHT_PUSH_SCRIPT, RIGHT_PUSH_INHERITANCE_TTL_SCRIPT, values);
    }

    @Override
    public boolean rSetValue(String key, int maxSize, String... value) {
        if (maxSize < 0) {
            return rSetValue(key, -1, value);
        }
        return pushValueSetMaxSizeByScript(key, maxSize, RIGHT_PUSH_TRIM_SCRIPT, value);
    }

    @Override
    public boolean rSetObject(String key, long expire, Object... values) {
        return rSetValue(key, expire, Arrays.stream(values).map(GsonUtil::objectToJson).collect(Collectors.joining()));
    }

    @Override
    public boolean rSetValue(String key, int maxSize, Object... values) {
        if (maxSize < 0) {
            return rSetValue(key, -1, Arrays.stream(values).map(GsonUtil::objectToJson).collect(Collectors.joining()));
        }
        return pushValueSetMaxSizeByScript(key, maxSize, RIGHT_PUSH_TRIM_SCRIPT, Arrays.stream(values).map(GsonUtil::objectToJson).collect(Collectors.joining()));
    }

    @Override
    public boolean hSetValue(String key, String subKey, String value, long expire) {
        return hSetValue(key, new LinkedHashMap<>(Map.of(subKey, value)), expire);
    }

    @Override
    public boolean hSetValue(String key, LinkedHashMap<String, String> values, long expire) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            if (expire > 0) connection.eval(MAP_SET_SCRIPT,
                    Stream.concat(
                            values.keySet().stream(),
                            Stream.of(key)
                    ).collect(Collectors.toList()),
                    Stream.concat(
                            values.values().stream(),
                            Stream.of(String.valueOf(expire))
                    ).collect(Collectors.toList()));
            else connection.eval(MAP_SET_INHERITANCE_TTL_SCRIPT,
                    Stream.concat(
                            values.keySet().stream(),
                            Stream.of(key)
                    ).collect(Collectors.toList()),
                    values.values().stream().toList());
            return true;
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return false;
    }

    @Override
    public boolean hSetObject(String key, String subKey, Object value, long expire) {
        return hSetObject(key, new LinkedHashMap<>(Map.of(subKey, value)), expire);
    }

    @Override
    public boolean hSetObject(String key, LinkedHashMap<String, Object> values, long expire) {
        return hSetValue(key, values.entrySet().stream().
                collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> GsonUtil.objectToJson(e.getValue()),
                    (oldValue, newValue) -> newValue, LinkedHashMap::new
        )), expire);
    }

    @Override
    public Optional<String> getValue(String key) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return Optional.empty();
        try (Jedis connection = connectionOptional.get()){
            return Optional.ofNullable(connection.get(key));
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getObject(String key, Class<T> clazz) {
        return getValue(key).map(serializerValue -> GsonUtil.jsonToObject(serializerValue, clazz));
    }

    @Override
    public Optional<String> hGetValue(String key, String subKey) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return Optional.empty();
        try (Jedis connection = connectionOptional.get()) {
            return Optional.ofNullable(connection.hget(key, subKey));
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> hGetObject(String key, String subKey, Class<T> clazz) {
        return hGetValue(key, subKey).map(serializerValue -> GsonUtil.jsonToObject(serializerValue, clazz));
    }

    @Override
    public Optional<List<String>> hGetAllValue(String key) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return Optional.empty();
        try (Jedis connection = connectionOptional.get()) {
                return connection.exists(key) ? Optional.of(new ArrayList<>(connection.hgetAll(key).values()))
                        : Optional.empty();
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<List<T>> hGetAllObject(String key, Class<T> clazz) {
        return hGetAllValue(key).map(serializerList -> serializerList.stream().
                map(serializerValue -> GsonUtil.jsonToObject(serializerValue, clazz))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<List<String>> getAllValue(String key) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return Optional.empty();
        try (Jedis connection = connectionOptional.get()) {
            return connection.exists(key) ? Optional.of(connection.lrange(key, 0, -1))
                    : Optional.empty();
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<List<T>> getAllObject(String key, Class<T> clazz) {
        return getAllValue(key).map(strings -> strings.stream().map(serializerValue -> GsonUtil.jsonToObject(serializerValue, clazz))
                .collect(Collectors.toList()));
    }

    @Override
    public boolean removeKey(String... key) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()) {
            connection.del(key);
            return true;
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return false;
    }

    @Override
    public boolean removeKeyByPrefix(String... prefix) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            for (String p : prefix) {
                Set<String> keys = connection.keys(p + "*");
                if (!keys.isEmpty()) {
                    connection.del(keys.toArray(new String[0]));
                }
            }
            return true;
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return false;
    }

    @Override
    public boolean removeHkey(String key, String... subKey) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()) {
            connection.hdel(key, subKey);
            return true;
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 查询异常请检查 Redis 服务", e);
        }
        return false;
    }

    @Override
    public void publishValue(String channel, String value) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return;
        try (Jedis connection = connectionOptional.get()){
            connection.publish(channel, value);
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 发布订阅发布时出现未知异常", e);
        }
    }

    @Override
    public boolean tryLock(String lockKey, String lockHolder, long expire) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            return connection.eval(DISTRIBUTED_LOCK_SCRIPT,
                    Collections.singletonList(lockKey), List.of(lockHolder, String.valueOf(expire))).equals(1L);
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 使用分布式锁时出现未知异常", e);
        }
        return false;
    }

    @Override
    public boolean releasedLock(String lockKey, String lockHolder) {
        Optional<Jedis> connectionOptional = getConnection();
        if (connectionOptional.isEmpty()) return false;
        try (Jedis connection = connectionOptional.get()){
            return connection.eval(RELEASED_LOCK_SCRIPT, Collections.singletonList(lockKey), Collections.singletonList(lockHolder)).equals(1L);
        } catch (Exception e) {
            PLUGIN.getLoggerManager().error("Redis 解锁分布式锁时出现未知异常", e);
        }
        return false;
    }

    static {
        /* Lua脚本 这个脚本首先检查键是否存在，
        如果存在则获取其TTL，
        并根据TTL值来决定是否设置新值以及新的过期时间 */
        SET_INHERITANCE_TTL_SCRIPT = "if redis.call('EXISTS', KEYS[1]) == 1 " +
                "then " +
                    "local TTL = redis.call('TTL', KEYS[1]) " +
                    "if TTL > -1 " +
                    "then " +
                        "redis.call('SET', KEYS[1], ARGV[1]) " +
                        "redis.call('EXPIRE', KEYS[1], TTL) " +
                    "end " +
                "end";

        /* Lua脚本 这个脚本用于像队列左侧插入数值，并且设置队列的过期时间 */
        LEFT_PUSH_SCRIPT = "for I = 1, #ARGV - 1 do " +
                "redis.call('LPUSH', KEYS[1], ARGV[I]) " +
                "end " +
                "redis.call('EXPIRE', KEYS[1], ARGV[#ARGV])";

        /* Lua脚本 这个脚本用于像队列左侧侧插入数值，并且设置队列的大小 */
        LEFT_PUSH_TRIM_SCRIPT = "for I = 1, #ARGV - 1 do " +
                "redis.call('LPUSH', KEYS[1], ARGV[I]) " +
                "end " +
                "redis.call('LTRIM', KEYS[1], 0, ARGV[#ARGV])";

        /* Lua脚本 这个脚本首先检查键是否存在，
        如果存在则获取其TTL，
        并根据TTL值来决定是否像队列左侧插入新值以及新的过期时间 */
        LEFT_PUSH_INHERITANCE_TTL_SCRIPT = "if redis.call('EXISTS', KEYS[1]) == 1 " +
                "then " +
                    "local TTL = redis.call('TTL', KEYS[1]) " +
                    "if TTL > -1 " +
                    "then " +
                        "for I = 1, #ARGV do" +
                            "redis.call('LPUSH', KEYS[1], ARGV[I]) " +
                        "end " +
                        "redis.call('EXPIRE', KEYS[1], TTL) " +
                    "end " +
                "end";

        /* Lua脚本 这个脚本用于像队列右侧插入数值，并且设置队列的过期时间 */
        RIGHT_PUSH_SCRIPT = "for I = 1, #ARGV - 1 do " +
                "redis.call('RPUSH', KEYS[1], ARGV[I]) " +
                "end " +
                "redis.call('EXPIRE', KEYS[1], ARGV[#ARGV])";

        /* Lua脚本 这个脚本用于像队列右侧插入数值，并且设置队列的大小 */
        RIGHT_PUSH_TRIM_SCRIPT = "for I = 1, #ARGV - 1 do " +
                "redis.call('RPUSH', KEYS[1], ARGV[I]) " +
                "end " +
                "redis.call('LTRIM', KEYS[1], 0, ARGV[#ARGV])";

        /* Lua脚本 这个脚本首先检查键是否存在，
        如果存在则获取其TTL，
        并根据TTL值来决定是否像队列右侧插入新值以及新的过期时间 */
        RIGHT_PUSH_INHERITANCE_TTL_SCRIPT = "if redis.call('EXISTS', KEYS[1]) == 1 " +
                "then " +
                    "local TTL = redis.call('TTL', KEYS[1]) " +
                    "if TTL > -1 " +
                    "then " +
                        "for I = 1, #ARGV do" +
                            "redis.call('RPUSH', KEYS[1], ARGV[I]) " +
                        "end " +
                        "redis.call('EXPIRE', KEYS[1], TTL) " +
                    "end " +
                "end";

        /* Lua脚本 这个脚本用于像Map中插入数值，并且设置Map的过期时间 */
        MAP_SET_SCRIPT = "if #KEYS == #ARGV " +
                "then " +
                    "for I = 1, #KEYS - 1 " +
                        "do " +
                            "redis.call('HSET', KEYS[#KEYS], KEYS[I], ARGV[I]) " +
                        "end " +
                    "redis.call('EXPIRE', KEYS[#KEYS], ARGV[#ARGV]) " +
                "end";

        /* Lua脚本 这个脚本用于像Map中插入数值，并且设置Map的过期时间 */
        MAP_SET_INHERITANCE_TTL_SCRIPT = "if redis.call('EXISTS', KEYS[#KEYS]) == 1 and #KEYS - 1 == #ARGV " +
                "then " +
                    "local TTL = redis.call('TTL', KEYS[#KEYS]) " +
                    "if TTL > -1 " +
                        "then " +
                            "for I = 1, #KEYS - 1 do " +
                                "redis.call('HSET', KEYS[#KEYS], KEYS[I], ARGV[I]) " +
                             "end " +
                            "redis.call('EXPIRE', KEYS[#KEYS], TTL) " +
                        "end " +
                "end";
        /* Lua脚本 这个脚本用于分布式锁上锁 */
        DISTRIBUTED_LOCK_SCRIPT = "if redis.call('SET', KEYS[1], ARGV[1], 'NX') then " +
                    "redis.call('EXPIRE', KEYS[1], ARGV[2]) " +
                    "return 1 else return 0 end";
        /* Lua脚本 这个脚本用于分布式锁解锁 */
        RELEASED_LOCK_SCRIPT = "if redis.call('GET',KEYS[1]) == ARGV[1] then " +
                "return redis.call('DEL', KEYS[1]) else return 0 end";
    }
}
