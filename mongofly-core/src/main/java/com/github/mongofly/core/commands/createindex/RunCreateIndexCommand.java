package com.github.mongofly.core.commands.createindex;

import com.github.mongofly.core.commands.RunCommand;
import com.github.mongofly.core.commands.utils.GetCollectionNameFromCommand;
import com.github.mongofly.core.commands.utils.GetMongoCollection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class RunCreateIndexCommand implements RunCommand {

    private MongoDatabase db;

    public RunCreateIndexCommand(MongoDatabase db) {

        this.db = db;
    }

    @Override
    public void run(String command) {

        CreateIndexObject createIndexObject = CreateIndexConvert.convert(command);

        String collectionName = GetCollectionNameFromCommand.get(command);

        MongoCollection<Document> collection = GetMongoCollection.get(db, collectionName);

        if(createIndexObject.getIndexOptions().isPresent()) {

            collection.createIndex(createIndexObject.getKeys(), createIndexObject.getIndexOptions().get());

        } else {

            collection.createIndex(createIndexObject.getKeys());

        }
    }

}
