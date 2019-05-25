package com.github.mongofly.core.commands.update;

import com.github.mongofly.core.commands.utils.GetMongoCollection;
import com.github.mongofly.core.commands.RunCommand;
import com.github.mongofly.core.commands.utils.GetCollectionNameFromCommand;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class RunUpdateCommand implements RunCommand {

    private MongoDatabase db;

    public RunUpdateCommand(MongoDatabase db) {

        this.db = db;
    }

    @Override
    public void run(String command) {

        UpdateObject updateObject = UpdateConvert.convert(command);

        String collectionName = GetCollectionNameFromCommand.get(command);

        MongoCollection<Document> collection = GetMongoCollection.get(db, collectionName, updateObject.getWriteConcern());

        if (updateObject.isMulti()) {

            if (updateObject.getUpdateOptions().isPresent()) {

                collection.updateMany(updateObject.getQuery(), updateObject.getUpdate(), updateObject.getUpdateOptions().get());

            } else {

                collection.updateMany(updateObject.getQuery(), updateObject.getUpdate());
            }

        } else {

            if (updateObject.getUpdateOptions().isPresent()) {

                collection.updateOne(updateObject.getQuery(), updateObject.getUpdate(), updateObject.getUpdateOptions().get());

            } else {

                collection.updateOne(updateObject.getQuery(), updateObject.getUpdate());
            }
        }
    }

}
