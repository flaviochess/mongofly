package com.github.mongofly.core.commands.remove;

import com.mongodb.WriteConcern;
import com.mongodb.client.model.DeleteOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class DeleteObject {

    private Document query;

    private Optional<DeleteOptions> deleteOptions;

    private Optional<WriteConcern> writeConcern;

    private boolean justOne;
}
