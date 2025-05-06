package com.magicrealms.magiclib.common.repository;

import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.common.utils.MongoDBUtil;
import com.magicrealms.magiclib.common.utils.RedissonUtil;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.Getter;
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

    private static final String cacheHkey_TEMPLATE = "MONGO_DB_TABLE_%s";

    @Getter
    private final MongoDBStore mongoDBStore;

    @Getter
    private final RedisStore redisStore;

    @Getter
    private final String tableName;

    @Getter
    private final String cacheHkey;

    @Getter
    private final long cacheExpire;

    private final String ID_FIELD_NAME;

    private final Class<T> CLAZZ;

    public BaseRepository(MongoDBStore mongoDBStore, String tableName,
                          @Nullable RedisStore redisStore, long cacheExpire,  Class<T> clazz) {
        this.mongoDBStore = mongoDBStore;
        this.redisStore = redisStore;
        this.tableName = tableName;
        this.cacheHkey = String.format(cacheHkey_TEMPLATE, StringUtils.upperCase(tableName));
        this.cacheExpire = cacheExpire;
        this.CLAZZ = clazz;
        this.ID_FIELD_NAME = MongoDBUtil.getIdFieldName(CLAZZ).orElse(null);
        mongoDBStore.createTable(tableName);
    }

    @Override
    public void insert(T entity) {
        mongoDBStore.insertOne(tableName, MongoDBUtil.toDocument(entity));
    }

    @Override
    public T queryById(Object id) {
        if (id == null || StringUtils.isBlank(ID_FIELD_NAME)) {
            return null;
        }
        String subKey = String.valueOf(id);
        Optional<T> redisData = Optional.ofNullable(redisStore)
                .flatMap(r -> r.hGetObject(cacheHkey, subKey, CLAZZ));
        if (redisData.isPresent()) {
            return redisData.get();
        }
        try (MongoCursor<Document> iterator = mongoDBStore
                .select(tableName, Filters.eq(ID_FIELD_NAME, id))) {
            if (iterator.hasNext()) {
                T data = MongoDBUtil.toObject(iterator.next(), CLAZZ);
                Optional.ofNullable(redisStore)
                        .ifPresent(r -> r.hSetObject(cacheHkey, subKey, data, cacheExpire));
                return data;
            }
        } finally {
            mongoDBStore.close();
        }
        return null;
    }

    @Override
    public void updateById(Object id, Consumer<T> consumer) {
        if (id == null || StringUtils.isBlank(ID_FIELD_NAME)) {
            return;
        }
        String subKey = String.valueOf(id);
        RedissonUtil.doAsyncWithLock(redisStore,
                String.format(cacheHkey + "_LOCK_%s", subKey),
                subKey,
                5000, () -> {
                    /* 修改表字段 */
                    T data = queryById(id);
                    if (data == null) {
                        return;
                    }
                    consumer.accept(data);
                    if (!mongoDBStore.updateOne(tableName,
                            Filters.eq(ID_FIELD_NAME, id),
                            new Document("$set", MongoDBUtil.toDocument(data)))) {
                        return;
                    }
                    redisStore.hGetObject(cacheHkey, subKey, CLAZZ).ifPresent(
                            e -> {
                                consumer.accept(e);
                                redisStore.hSetObject(cacheHkey, subKey, data, cacheExpire);
                            }
                    );
                });
    }

    @Override
    public void deleteById(Object id) {
        String subKey = String.valueOf(id);
        if (mongoDBStore.deleteOne(tableName, Filters.eq(ID_FIELD_NAME, id))) {
            redisStore.removeHkey(cacheHkey, subKey);
        }
    }
}
