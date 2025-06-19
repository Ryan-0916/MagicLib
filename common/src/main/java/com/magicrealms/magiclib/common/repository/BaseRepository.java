package com.magicrealms.magiclib.common.repository;

import com.magicrealms.magiclib.common.annotations.MongoId;
import com.magicrealms.magiclib.common.store.MongoDBStore;
import com.magicrealms.magiclib.common.store.RedisStore;
import com.magicrealms.magiclib.common.utils.MongoDBUtil;
import com.magicrealms.magiclib.common.utils.RedissonUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public abstract class BaseRepository<T> implements IBaseRepository<T> {

    private static final String CACHE_HKEY_TEMPLATE = "MONGO_DB_TABLE_%s";
    private static final String LOCK_KEY_TEMPLATE = "%s_LOCK_%s";
    private static final long DEFAULT_LOCK_TIMEOUT = 5000L;

    protected final MongoDBStore mongoDBStore;
    protected final RedisStore redisStore;
    protected final String tableName;
    protected final String cacheHkey;
    protected final long cacheExpire;
    protected final boolean autoCache;
    protected final MongoId filedId;
    protected final Class<T> entityClass;

    public BaseRepository(MongoDBStore mongoDBStore, String tableName,
                          RedisStore redisStore, Class<T> clazz) {
        this(mongoDBStore, tableName, redisStore, false, 0L, clazz);
    }

    public BaseRepository(MongoDBStore mongoDBStore, String tableName,
                          RedisStore redisStore, boolean autoCache,
                          long cacheExpire, Class<T> clazz) {
        this.mongoDBStore = mongoDBStore;
        this.redisStore = redisStore;
        this.tableName = tableName;
        this.cacheHkey = String.format(CACHE_HKEY_TEMPLATE, StringUtils.upperCase(tableName));
        this.autoCache = autoCache;
        this.cacheExpire = cacheExpire;
        this.entityClass = clazz;
        this.filedId = MongoDBUtil.getFiledId(clazz).orElse(null);
        this.mongoDBStore.createCollection(tableName);
    }

    public boolean isAutoCache() {
        return autoCache && cacheExpire > 0;
    }

    protected void cacheEntity(String id, T entity) {
        if (!isAutoCache()) {
            return;
        }
        redisStore.hSetObject(cacheHkey, id, entity, cacheExpire);
    }

    protected void invalidateCache(String id) {
        if (isAutoCache()) {
            redisStore.removeHkey(cacheHkey, id);
        }
    }

    protected Bson getIdFilter(Object id) {
        return filedId.ignoreCase() ? Filters.regex(filedId.filedName(),
                "^" + Pattern.quote(id.toString()) + "$", "i")
                : Filters.eq(filedId.filedName(), id);
    }

    @Override
    public void insert(T entity) {
        mongoDBStore.insertOne(tableName, MongoDBUtil.toDocument(entity));
    }

    @Override
    public T queryById(Object id) {
        if (id == null || filedId == null) {
            return null;
        }
        String subKey = filedId.ignoreCase() ? StringUtils.upperCase(id.toString())
                : id.toString();
        if (isAutoCache()) {
            Optional<T> cachedData = redisStore.hGetObject(cacheHkey, subKey, entityClass);
            if (cachedData.isPresent()) {
                return cachedData.get();
            }
        }
        try (MongoCursor<Document> cursor = mongoDBStore.find(tableName, getIdFilter(id))) {
            if (cursor.hasNext()) {
                T data = MongoDBUtil.toObject(cursor.next(), entityClass);
                cacheEntity(subKey, data);
                return data;
            }
        }
        return null;
    }

    public CompletableFuture<Boolean> asyncUpdateById(Object id, Consumer<T> updater) {
        return CompletableFuture.supplyAsync(() -> updateById(id, updater));
    }

    public boolean updateById(Object id, Consumer<T> updater) {
        if (id == null || filedId == null) {
            return false;
        }
        String subKey = filedId.ignoreCase() ? StringUtils.upperCase(id.toString())
                : id.toString();
        String lockKey = String.format(LOCK_KEY_TEMPLATE, cacheHkey, subKey);
        try {
            return RedissonUtil.doWithLock(redisStore, lockKey, subKey,
                    DEFAULT_LOCK_TIMEOUT, () -> {
                        T data = queryById(id);
                        if (data == null) {
                            return false;
                        }
                        updater.accept(data);
                        boolean updateSuccess = mongoDBStore.updateOne(
                                tableName,
                                getIdFilter(id),
                                new Document("$set", MongoDBUtil.toDocument(data))
                        );
                        if (!updateSuccess) {
                            return false;
                        }
                        if (isAutoCache()) {
                            redisStore.hGetObject(cacheHkey, subKey, entityClass)
                                    .ifPresent(cachedEntity -> {
                                        updater.accept(cachedEntity);
                                        cacheEntity(subKey, cachedEntity);
                                    });
                        }
                        return true;
                    });
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void deleteById(Object id) {
        if (id == null || filedId == null) {
            return;
        }
        String subKey = filedId.ignoreCase() ? StringUtils.upperCase(id.toString())
                : id.toString();
        if (mongoDBStore.deleteOne(tableName, getIdFilter(id))) {
            invalidateCache(subKey);
        }
    }
}