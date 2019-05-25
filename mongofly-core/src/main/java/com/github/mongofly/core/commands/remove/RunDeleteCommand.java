package com.github.mongofly.core.commands.remove;

import com.github.mongofly.core.commands.GetMongoCollection;
import com.github.mongofly.core.commands.RunCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class RunDeleteCommand implements RunCommand {

    private final MongoDatabase db;

    public RunDeleteCommand(MongoDatabase db) {

        this.db = db;
    }

    @Override
    public void run(String command) {

        DeleteObject deleteObject = RemoveConvert.convert(command);

        String collectionName = GetCollectionNameFromCommand.get(command);

        MongoCollection<Document> collection = GetMongoCollection.get(db, collectionName, deleteObject.getWriteConcern());

        if (deleteObject.isJustOne()) {

            if (deleteObject.getDeleteOptions().isPresent()) {

                collection.deleteOne(deleteObject.getQuery(), deleteObject.getDeleteOptions().get());

            } else {

                collection.deleteOne(deleteObject.getQuery());
            }

        } else {

            if (deleteObject.getDeleteOptions().isPresent()) {

                collection.deleteMany(deleteObject.getQuery(), deleteObject.getDeleteOptions().get());

            } else {

                collection.deleteMany(deleteObject.getQuery());
            }
            
        }
    }
}
