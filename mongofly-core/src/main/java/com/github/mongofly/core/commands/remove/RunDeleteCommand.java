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

        Delete delete = RemoveConvert.convert(command);

        String collectionName = GetCollectionNameFromCommand.get(command);

        MongoCollection<Document> collection = GetMongoCollection.get(db, collectionName, delete.getWriteConcern());

        if (delete.isJustOne()) {

            if (delete.getDeleteOptions().isPresent()) {

                collection.deleteOne(delete.getQuery(), delete.getDeleteOptions().get());

            } else {

                collection.deleteOne(delete.getQuery());
            }

        } else {

            if (delete.getDeleteOptions().isPresent()) {

                collection.deleteMany(delete.getQuery(), delete.getDeleteOptions().get());

            } else {

                collection.deleteMany(delete.getQuery());
            }
            
        }
    }
}
