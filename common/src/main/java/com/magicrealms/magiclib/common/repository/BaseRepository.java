package com.magicrealms.magiclib.common.repository;

import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.common.utils.MongoDBUtil;
import com.magicrealms.magiclib.common.utils.RedissonUtil;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Ryan-0916
 * @Desc 说明
 * @date 2025-05-06
 */
@SuppressWarnings("unused")
public abstract class BaseRepository<T> implements IBaseRepository<T>{

    private static final String CACHE_HKEY_TEMPLATE = "MONGO_DB_TABLE_%s";

    private final MongoDBStore MONGO_DB_STORE;

    private final RedisStore REDIS_STORE;

    private final String TABLE_NAME;

    private final String CACHE_HKEY;

    private final Class<T> CLAZZ;

    private final long CACHE_EXPIRE;

    private final String ID_FIELD_NAME;


    public BaseRepository(MongoDBStore mongoDBStore, String tableName, Class<T> clazz,
                          @Nullable RedisStore redisStore, long cacheExpire) {
        this.MONGO_DB_STORE = mongoDBStore;
        this.REDIS_STORE = redisStore;
        this.TABLE_NAME = tableName;
        this.CACHE_HKEY = String.format(CACHE_HKEY_TEMPLATE, StringUtils.upperCase(tableName));
        this.CLAZZ = clazz;
        this.CACHE_EXPIRE = cacheExpire;
        this.ID_FIELD_NAME = MongoDBUtil.getIdFieldName(CLAZZ).orElse(null);
        MONGO_DB_STORE.createTable(TABLE_NAME);
    }

    @Override
    public T queryById(Object id) {
        if (id == null || StringUtils.isBlank(ID_FIELD_NAME)) {
            return null;
        }
        String subKey = String.valueOf(id);
        Optional<T> redisData = Optional.ofNullable(REDIS_STORE)
                .flatMap(r -> r.hGetObject(CACHE_HKEY, subKey, CLAZZ));
        if (redisData.isPresent()) {
            return redisData.get();
        }
        try (MongoCursor<Document> iterator = MONGO_DB_STORE
                .select(TABLE_NAME, Filters.eq(ID_FIELD_NAME, id))) {
            if (iterator.hasNext()) {
                T data = MongoDBUtil.toObject(iterator.next(), CLAZZ);
                Optional.ofNullable(REDIS_STORE)
                        .ifPresent(r -> r.hSetObject(CACHE_HKEY, subKey, data, CACHE_EXPIRE));
                return data;
            }
        } finally {
            MONGO_DB_STORE.close();
        }
        return null;
    }

    @Override
    public void updateById(Object id, Consumer<T> consumer) {
        if (id == null || StringUtils.isBlank(ID_FIELD_NAME)) {
            return;
        }
        String subKey = String.valueOf(id);
        RedissonUtil.doAsyncWithLock(REDIS_STORE,
                String.format(CACHE_HKEY + "_LOCK_%s", subKey),
                subKey,
                5000, () -> {
                    /* 修改表字段 */
                    T data = queryById(id);
                    if (data == null) {
                        return;
                    }
                    consumer.accept(data);
                    if (!MONGO_DB_STORE.updateOne(TABLE_NAME, Filters.eq(ID_FIELD_NAME, id),
                            MongoDBUtil.toDocument(consumer))) {
                        return;
                    }
                    REDIS_STORE.hGetObject(CACHE_HKEY, subKey, CLAZZ).ifPresent(
                            e -> {
                                consumer.accept(e);
                                REDIS_STORE.hSetObject(CACHE_HKEY, subKey, data, CACHE_EXPIRE);
                            }
                    );
                });
    }

    @Override
    public void deleteById(Object id) {
        String subKey = String.valueOf(id);
        if (MONGO_DB_STORE.deleteOne(TABLE_NAME, Filters.eq(ID_FIELD_NAME, id))) {
            REDIS_STORE.removeHkey(CACHE_HKEY, subKey);
        }
    }
}
