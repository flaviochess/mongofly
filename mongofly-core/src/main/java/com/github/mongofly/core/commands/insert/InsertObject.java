package com.github.mongofly.core.commands.insert;

import com.mongodb.WriteConcern;
import com.mongodb.client.model.InsertManyOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class InsertObject {

    private List<Document> documents;

    private Optional<InsertManyOptions> insertOptions;

    private Optional<WriteConcern> writeConcern;

}
