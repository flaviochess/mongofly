package com.github.mongofly.core.commands.insert;

import com.github.mongofly.core.commands.GetMongoCollection;
import com.github.mongofly.core.commands.RunCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import org.bson.Document;

import java.util.List;

import static com.github.mongofly.core.converts.CommandConvert.DOCUMENTS_LIMIT_SIZE;

public class RunInsertCommand implements RunCommand {

    private MongoDatabase db;

    public RunInsertCommand(MongoDatabase db) {

        this.db = db;
    }

    public void run(String command) {

        InsertObject insertObject = InsertConvert.convert(command);

        String collectionName = GetCollectionNameFromCommand.get(command);

        MongoCollection<Document> collection = GetMongoCollection.get(db, collectionName, insertObject.getWriteConcern());

        List<List<Document>> partitionedDocuments = partitionDocuments(insertObject.getDocuments());

        partitionedDocuments.forEach(documents -> {
            collection.insertMany(documents, insertObject.getInsertOptions().orElse(new InsertManyOptions()));
        });

    }

    private List<List<Document>> partitionDocuments(List<Document> documents) {

        return Lists.partition(documents, DOCUMENTS_LIMIT_SIZE);
    }

}
