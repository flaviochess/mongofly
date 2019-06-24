package com.github.mongofly.core.commands.createindex;

import com.mongodb.client.model.IndexOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class CreateIndexObject {

    private Document keys;

    private Optional<IndexOptions> indexOptions;
}
