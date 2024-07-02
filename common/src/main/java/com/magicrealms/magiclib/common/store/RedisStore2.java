//package com.magicrealms.magiclib.common.store;
//
//import com.magicrealms.magiclib.common.MagicRealmsPlugin;
//import com.magicrealms.magiclib.common.utils.JsonUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import redis.clients.jedis.Jedis;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author Ryan-0916
// * @Desc Redis缓存相关操作
// * 以下操作皆采用短连接
// * @date 2023-10-01
// */
//@SuppressWarnings("unused")
//public class RedisStore2 {
//
//    private static final String LOCK_SUCCESS = "OK";
//    private static final String SET_IF_NOT_EXIST = "NX";
//    private static final String SET_WITH_EXPIRE_TIME = "PX";
//    private final MagicRealmsPlugin PLUGIN;
//    private final String HOST;
//    private final int PORT;
//    private final String PASSWORD;
//
//    public RedisStore2(MagicRealmsPlugin plugin, String host, int port, String password) {
//        this.PLUGIN = plugin;
//        this.HOST = host;
//        this.PORT = port;
//        this.PASSWORD = password;
//    }
//
//    private Jedis getConnection() {
//        try {
//            Jedis connection = new Jedis(HOST, PORT);
//            connection.auth(PASSWORD);
//            return connection;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 连接异常请检查 Redis 服务");
//            return null;
//        }
//    }
//
//    @NotNull
//    public <T> List<T> selectLimit(@NotNull String key, int start, int end, @NotNull Class<T> clazz) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                return JsonUtil.jsonToList(connection.lrange(key, start, end), clazz);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return new ArrayList<>();
//    }
//
//    @Nullable
//    public <T> T selectIndexOf(@NotNull String key, int index, @NotNull Class<T> clazz) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                return JsonUtil.jsonToObject(connection.lindex(key, index), clazz);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return null;
//    }
//
//    @NotNull
//    public <T> List<T> selectAll(@NotNull String key, @NotNull Class<T> clazz) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                return JsonUtil.jsonToList(connection.lrange(key, 0, -1), clazz);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return new ArrayList<>();
//    }
//
//    @Nullable
//    public <T> T selectOne(@NotNull String key, @NotNull Class<T> clazz) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                return JsonUtil.jsonToObject(connection.get(key), clazz);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return null;
//    }
//
//    @Nullable
//    public String selectValue(@NotNull String key) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                return connection.get(key);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return null;
//    }
//
//    /**
//     * 查询阻塞
//     * @param key key
//     * @param timeout 超时时间
//     * @return 队列中的内容
//     */
//    @Nullable
//    public List<String> selectBlocking(@NotNull String key, int timeout){
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                return connection.brpop(timeout, key);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return null;
//    }
//
//    public boolean exists(@NotNull String key) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                return connection.exists(key);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 像缓存中添加一条数据
//     * @param key 此数据的 Key
//     * @param value 数据
//     * @param seconds 存储秒数如果小于 0，则不去更改原先 key 的缓存时间
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean insertValue(@NotNull String key, @NotNull String value, long seconds) {
//        try (Jedis connection = getConnection()){
//            if (connection == null) {
//                return false;
//            }
//            if (seconds > 0) {
//                connection.setex(key, seconds, value);
//            } else {
//                long ttl = connection.ttl(key);
//                connection.setex(key, (ttl <= 0 ? 60L : ttl), value);
//            }
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 像缓存中添加一个对象
//     * @param key 此数据的 Key
//     * @param object 对象
//     * @param seconds 存储秒数如果小于 0，则不去更改原先 key 的缓存时间
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean insertObject(@NotNull String key, @NotNull Object object, long seconds) {
//        String value = JsonUtil.objectToJson(object);
//        if (StringUtils.isBlank(value)) {
//            return false;
//        }
//        return insertValue(key, value, seconds);
//    }
//
//    /**
//     * 像队列种添加一条数据
//     * @param key 队列 Key
//     * @param value 数据
//     * @param seconds 存储秒数如果小于 0，则不去更改原先 key 的缓存时间
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean pushValue(@NotNull String key, @NotNull String value, long seconds) {
//        try (Jedis connection = getConnection()){
//            if (connection == null) {
//                return false;
//            }
//            if (seconds > 0) {
//                connection.rpush(key, value);
//                connection.expire(key, seconds);
//            } else {
//                long ttl = connection.ttl(key);
//                connection.rpush(key, value);
//                connection.expire(key, ttl <= 0 ? 60 : ttl);
//            }
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 像队列种添加一条对象
//     * @param key 队列 Key
//     * @param object 对象
//     * @param seconds 存储秒数如果小于 0，则不去更改原先 key 的缓存时间
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean pushObject(@NotNull String key, @NotNull Object object, long seconds) {
//        try (Jedis connection = getConnection()){
//            String value = JsonUtil.objectToJson(object);
//            if (connection == null || StringUtils.isBlank(value)){
//                return false;
//            }
//            if (seconds > 0) {
//                connection.rpush(key, value);
//                connection.expire(key, seconds);
//            } else {
//                long ttl = connection.ttl(key);
//                connection.rpush(key, value);
//                connection.expire(key, ttl <= 0 ? 60 : ttl);
//            }
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 像队列种添加多条对象
//     * @param key 队列 Key
//     * @param objects 对象列表
//     * @param seconds 存储秒数如果小于 0，则不去更改原先 key 的缓存时间
//     * @param <T> 添加列表的类型参数
//     * @return 是否成功 {@link Boolean}
//     */
//    public <T> boolean pushAll(@NotNull String key, @NotNull List<T> objects, long seconds, boolean removeOld) {
//        try (Jedis connection = getConnection()){
//            if (connection == null) {
//                return false;
//            }
//            String[] value = objects.stream().filter(Objects::nonNull).map(JsonUtil::objectToJson).toArray(String[]::new);
//            if (value.length > 0) {
//                if (removeOld) {
//                    connection.del(key);
//                }
//                if (seconds > 0) {
//                    connection.rpush(key, value);
//                    connection.expire(key, seconds);
//                } else {
//                    long ttl = connection.ttl(key);
//                    connection.rpush(key, value);
//                    connection.expire(key, ttl <= 0 ? 60 : ttl);
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 更改队列里的某一个下标的内容
//     * @param key 队列 Key
//     * @param value 对象的值
//     * @param index 对象的下标
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean updateValue(@NotNull String key, @NotNull String value, int index) {
//        try (Jedis connection = getConnection()){
//            if (connection == null) {
//                return false;
//            }
//            if (connection.exists(key)) {
//                connection.lset(key, index, value);
//            }
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 更改队列里的某一个下标的内容
//     * @param key 队列 Key
//     * @param object 对象
//     * @param index 对象的下标
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean updateOne(@NotNull String key, @NotNull Object object, int index) {
//        try (Jedis connection = getConnection()){
//            String value = JsonUtil.objectToJson(object);
//            if (connection == null || StringUtils.isBlank(value)) {
//                return false;
//            }
//            if (connection.exists(key)) {
//                connection.lset(key, index, value);
//            }
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 移除一条数据
//     * @param key 数据 Key
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean removeOne(@NotNull String key) {
//        try (Jedis connection = getConnection()){
//            if (connection == null) {
//                return false;
//            }
//            connection.del(key);
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 移除多条条数据
//     * @param key 数据 Key
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean removeMany(@NotNull String... key) {
//        try (Jedis connection = getConnection()){
//            if (connection == null) {
//                return false;
//            }
//            connection.del(key);
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 通过下标移除队列中的某一项
//     * @param key 元素
//     * @param index 下标
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean removeByIndex(@NotNull String key, int index) {
//        try (Jedis connection = getConnection()){
//            if (connection == null) {
//                return false;
//            }
//            String value = connection.lindex(key, index);
//            if (StringUtils.isBlank(value)) {
//                return false;
//            }
//            connection.lrem(key, 1, value);
//            return true;
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    public boolean removeOneByPrefix(String prefix) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                String[] keys = connection.keys(prefix).toArray(new String[0]);
//                if (keys.length > 0) {
//                    connection.del(keys);
//                }
//                return true;
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    public boolean removeManyByPrefix(String... prefix) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                for (String p : prefix) {
//                    String[] keys = connection.keys(p).toArray(new String[0]);
//                    if (keys.length > 0) {
//                        connection.del(keys);
//                    }
//                }
//                return true;
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 发布订阅 - 发布消息
//     * @param channel 消息频道
//     * @param msg 消息内容
//     */
//    public void publishMsg(@NotNull String channel, @NotNull String msg) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                connection.publish(channel, msg);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//    }
//
//    /**
//     * 尝试使用分布式锁
//     * @param key 锁的 KEY
//     * @param seconds 过期时间
//     * @param value 锁的主人
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean tryLock(String key, String value, long seconds) {
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                boolean v = connection.setnx(key, value) == 1;
//                if (v) {
//                    connection.expire(key, seconds);
//                }
//                return v;
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 尝试释放锁
//     * @param key 锁 Key
//     * @param value 锁的主人
//     * @return 是否成功 {@link Boolean}
//     */
//    public boolean releasedLock(String key, String value){
//        try (Jedis connection = getConnection()){
//            if (connection != null) {
//                String luaScripts = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//                Object result = connection.eval(luaScripts, Collections.singletonList(key), Collections.singletonList(value));
//                return result.equals(1L);
//            }
//        } catch (Exception e) {
//            PLUGIN.getLoggerManager().warning("Redis 查询异常请检查 Redis 服务" + e.getMessage());
//        }
//        return false;
//    }
//}
