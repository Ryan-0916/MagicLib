package com.magicrealms.magiclib.common.store;

import com.magicrealms.magiclib.common.exception.DataAccessException;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@SuppressWarnings("unused")
public class MongoDBStore {
    private static final String CONNECTION_ERROR = "MongoDB operation failed - {}";

    private final String databaseName;
    private final MongoClientSettings clientSettings;
    private MongoClient mongoClient;

    @Getter
    private MongoDatabase database;

    public MongoDBStore(String host, int port, String databaseName) {
        Objects.requireNonNull(host, "Host cannot be null");
        this.databaseName = Objects.requireNonNull(databaseName, "Database name cannot be null");
        this.clientSettings = MongoClientSettings.builder()
                .applyToClusterSettings(builder ->
                        builder.hosts(List.of(new ServerAddress(host, port))))
                .build();
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            this.mongoClient = MongoClients.create(clientSettings);
            this.database = mongoClient.getDatabase(databaseName);
        } catch (MongoException e) {
            log.error("Failed to initialize MongoDB connection", e);
            throw new IllegalStateException("MongoDB connection failed", e);
        }
    }

    public void destroy() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
            }
        } catch (Exception e) {
            log.warn("Error closing MongoDB connection", e);
        }
    }

    public void createCollection(String collectionName) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        try {
            if (!collectionExists(collectionName)) {
                database.createCollection(collectionName);
                log.info("Created MongoDB collection: {}", collectionName);
            }
        } catch (MongoException e) {
            log.error(CONNECTION_ERROR, "create collection", e);
            throw new DataAccessException("Failed to create collection", e);
        }
    }

    private boolean collectionExists(String collectionName) {
        return database.listCollectionNames()
                .into(new ArrayList<>())
                .contains(collectionName);
    }

    public void insertOne(String collectionName, Document document) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        Objects.requireNonNull(document, "Document cannot be null");
        try {
            getCollection(collectionName).insertOne(document);
        } catch (MongoException e) {
            log.error(CONNECTION_ERROR, "insert document", e);
        }
    }

    public MongoCursor<Document> find(String collectionName, Bson filter) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        try {
            return getCollection(collectionName)
                    .find(filter).iterator();
        } catch (MongoException e) {
            log.error(CONNECTION_ERROR, "find documents", e);
            throw new DataAccessException("Failed to query documents", e);
        }
    }

    public Optional<Document> findFirst(String collectionName) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");

        try {
            return Optional.ofNullable(getCollection(collectionName).find().first());
        } catch (MongoException e) {
            log.error(CONNECTION_ERROR, "find first document", e);
            return Optional.empty();
        }
    }

    public boolean updateOne(String collectionName, Bson filter, Bson update) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");

        try {
            UpdateResult result = getCollection(collectionName).updateOne(filter, update);
            return result.getMatchedCount() > 0;
        } catch (MongoException e) {
            log.error(CONNECTION_ERROR, "update document", e);
            return false;
        }
    }

    public boolean updateMany(String collectionName, Bson filter, Bson update) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        try {
            UpdateResult result = getCollection(collectionName).updateMany(filter, update);
            return result.getMatchedCount() > 0;
        } catch (MongoException e) {
            log.error(CONNECTION_ERROR, "update documents", e);
            return false;
        }
    }

    public boolean deleteOne(String collectionName, Bson filter) {
        Objects.requireNonNull(collectionName, "Collection name cannot be null");
        try {
            DeleteResult result = getCollection(collectionName).deleteOne(filter);
            return result.getDeletedCount() > 0;
        } catch (MongoException e) {
            log.error(CONNECTION_ERROR, "delete document", e);
            return false;
        }
    }

    private MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }
}
