package com.github.mongofly.core.commands.insert;

import com.github.mongofly.core.commands.GetMongoCollection;
import com.github.mongofly.core.commands.RunCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.google.common.collect.Lists;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.github.mongofly.core.converts.CommandConvert.DOCUMENTS_LIMIT_SIZE;

public class RunInsertCommand implements RunCommand {

    private MongoDatabase db;

    public RunInsertCommand(MongoDatabase db) {

        this.db = db;
    }

    @Autowired
    public void run(String command) {

        InsertMany insertMany = InsertConvert.convert(command);

        String collectionName = GetCollectionNameFromCommand.get(command);

        MongoCollection<Document> collection = GetMongoCollection.get(db, collectionName, insertMany.getWriteConcern());

        List<List<Document>> partitionedDocuments = partitionDocuments(insertMany.getDocuments());

        partitionedDocuments.forEach(documents -> {
            collection.insertMany(documents, insertMany.getInsertOptions().orElse(new InsertManyOptions()));
        });

    }

    private List<List<Document>> partitionDocuments(List<Document> documents) {

        return Lists.partition(documents, DOCUMENTS_LIMIT_SIZE);
    }

}
