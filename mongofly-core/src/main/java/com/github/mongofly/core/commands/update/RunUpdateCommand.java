package com.github.mongofly.core.commands.update;

import com.github.mongofly.core.commands.GetMongoCollection;
import com.github.mongofly.core.commands.RunCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
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

        Update update = UpdateConvert.convert(command);

        String collectionName = GetCollectionNameFromCommand.get(command);

        MongoCollection<Document> collection = GetMongoCollection.get(db, collectionName, update.getWriteConcern());

        if (update.isMulti()) {

            if (update.getUpdateOptions().isPresent()) {

                collection.updateMany(update.getQuery(), update.getUpdate(), update.getUpdateOptions().get());

            } else {

                collection.updateMany(update.getQuery(), update.getUpdate());
            }

        } else {

            if (update.getUpdateOptions().isPresent()) {

                collection.updateOne(update.getQuery(), update.getUpdate(), update.getUpdateOptions().get());

            } else {

                collection.updateOne(update.getQuery(), update.getUpdate());
            }
        }
    }

}

// se o $in realmente não funcionar executar o commando runCommand do drive com o código no formato antigo
