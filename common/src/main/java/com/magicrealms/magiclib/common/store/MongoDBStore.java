package com.magicrealms.magiclib.common.store;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;


@Slf4j
@SuppressWarnings("unused")
public class MongoDBStore {

    @Getter
    private MongoDatabase database;
    private final String HOST;
    private final int PORT;
    private final String DATABASE;
    private MongoClient connection;


    public MongoDBStore(String host, int port, String database) {
        this.HOST = host;
        this.PORT = port;
        this.DATABASE = database;
    }

    public void getConnection(){
        try {
            this.connection = new MongoClient(HOST, PORT);
            this.database = connection.getDatabase(DATABASE);
        } catch (Exception e) {
            log.error("MongoDB 连接异常请检查 MongoDB 服务", e);
        }
    }

    /**
     * 关闭连接池
     */
    public void close(){
        try {
            if (connection != null){
                this.connection.close();
            }
            if (database != null){
                this.database = null;
            }
        } catch (Exception e) {
            log.error("MongoDB 关闭连接异常请检查 MongoDB 服务", e);
        }
    }

    /**
     * 创建表
     * @param tableName 表名
     */
    public void createTable(String tableName) {
        this.getConnection();
        try {
            for (String collectionName : database.listCollectionNames()) {
                if (collectionName.equals(tableName)) {
                    return;
                }
            }
            database.createCollection(tableName);
            log.info("MongoDB 表创建完毕，表名: {}", tableName);
        } catch (Exception e) {
            log.error("MongoDB 创表异常请检查 MongoDB 服务", e);
        } finally {
            this.close();
        }
    }

    /**
     * 插入数据
     * @param tableName 表名
     * @param document 数据
     */
    public boolean insertOne(String tableName, Document document) {
        this.getConnection();
        try {
            MongoCollection<Document> collection = database.getCollection(tableName);
            collection.insertOne(document);
            return true;
        } catch (Exception e) {
            log.error("MongoDB 插入数据异常请检查 MongoDB 服务", e);
            return false;
        } finally {
            this.close();
        }
    }

    /**
     * 查询
     * @param tableName 表名
     * @param bson 条件
     * @return {@link MongoCursor<Document>}
     */
    public MongoCursor<Document> select(String tableName, Bson bson){
        this.getConnection();
        try {
            MongoCollection<Document> collection = database.getCollection(tableName);
            return collection.find(bson).iterator();
        } catch (Exception e) {
            log.error("MongoDB 查询数据异常请检查 MongoDB 服务", e);
        }
        return null;
    }


    /**
     * 查询首条数据
     * @param tableName 表名
     */
    public Document selectFirst(String tableName){
        this.getConnection();
        try {
            MongoCollection<Document> collection = database.getCollection(tableName);
            return collection.find().first();
        } catch (Exception e) {
            log.error("MongoDB 查询数据异常请检查 MongoDB 服务", e);
        }
        return null;
    }

    /**
     * 修改
     * @param tableName 表名
     * @param where 条件
     * @param value 修改项
     */
    public boolean updateOne(String tableName, Bson where, Bson value) {
        this.getConnection();
        try {
            MongoCollection<Document> collection = database.getCollection(tableName);
            return collection.updateOne(
                    where,
                    value).getMatchedCount() > 0;
        } catch (Exception e) {
            log.error("MongoDB 修改数据异常请检查 MongoDB 服务", e);
        } finally {
            this.close();
        }
        return false;
    }

    /**
     * 修改全部
     * @param tableName 表名
     * @param where 条件
     * @param value 修改项
     */
    public boolean updateAll(String tableName, Bson where, Bson value) {
        this.getConnection();
        try {
            MongoCollection<Document> collection = database.getCollection(tableName);
            return collection.updateMany(
                    where,
                    value).getMatchedCount() > 0;
        } catch (Exception e) {
            log.error("MongoDB 修改数据异常请检查 MongoDB 服务", e);
        } finally {
            this.close();
        }
        return false;
    }

    /**
     * 删除
     * @param tableName 表名
     * @param where 条件
     */
    public boolean deleteOne(String tableName, Bson where) {
        this.getConnection();
        try {
            MongoCollection<Document> collection = database.getCollection(tableName);
            return collection.deleteOne(where).getDeletedCount() > 0;
        } catch (Exception e) {
            log.error("MongoDB 修改数据异常请检查 MongoDB 服务", e);
        } finally {
            this.close();
        }
        return false;
    }

}
