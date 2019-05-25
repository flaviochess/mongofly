package com.github.mongofly.core.commands.update;

import com.mongodb.WriteConcern;
import com.mongodb.client.model.UpdateOptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;

import java.util.Optional;

@Getter
@AllArgsConstructor
public class UpdateObject {

    private Document query;

    private Document update;

    private Optional<UpdateOptions> updateOptions;

    private Optional<WriteConcern> writeConcern;

    private boolean multi;

}
