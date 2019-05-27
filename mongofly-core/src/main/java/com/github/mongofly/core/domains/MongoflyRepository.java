package com.github.mongofly.core.domains;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.Optional;

@Slf4j
public class MongoflyRepository {

    private static final String MONGOFLY_COLLECTION = "mongofly";

    private MongoCollection<Document> mongoCollection;

    public MongoflyRepository(MongoDatabase db) {

        this.mongoCollection = db.getCollection(MONGOFLY_COLLECTION);
    }

    public Optional<Mongofly> findByVersion(String version) {

        Document search = new Document();
        search.put("version", version);

        FindIterable<Document> mongoflyIterable = mongoCollection.find(search);

        Mongofly mongofly = Mongofly.fromDocument(mongoflyIterable.first());
        return Optional.ofNullable(mongofly);
    }

    public void save(Mongofly mongofly) {


        mongoCollection.insertOne(mongofly.toDocument());
    }
}