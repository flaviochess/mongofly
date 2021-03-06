package com.github.mongofly.core.commands.utils;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Optional;

public class GetMongoCollection {

    public static MongoCollection get(MongoDatabase db, String collectionName, Optional<WriteConcern> writeConcern) {

        if (writeConcern.isPresent()) {
            return db.getCollection(collectionName).withWriteConcern(writeConcern.get());
        }

        return get(db, collectionName);
    }

    public static MongoCollection get(MongoDatabase db, String collectionName) {

        return db.getCollection(collectionName);
    }

}
