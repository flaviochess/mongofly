package com.github.mongofly.core.commands.remove;

import com.github.mongofly.core.commands.ConvertCommandBody;
import com.github.mongofly.core.converts.CommandBuilder;
import com.github.mongofly.core.converts.CommandConvert;
import com.github.mongofly.core.utils.GetBodyFromCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.github.mongofly.core.utils.MongoflyException;
import com.mongodb.DBObject;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
    Samples:

    db.runCommand(
       {
          delete: "orders",
          deletes: [ { q: { status: "D" }, limit: 1 } ]
       }
    )

    db.runCommand(
       {
          delete: "orders",
          deletes: [ { q: { }, limit: 0 } ],
          ordered: false,
          writeConcern: { w: "majority", wtimeout: 5000 }
       }
    )

    from:

    db.bios.remove( { } );

    db.products.remove(
        { qty: { $gt: 20 } }
    );

 */
public class RemoveConvert implements CommandConvert {

    private static final String CURLY_BRACES_CLOSE_COMMA = "},";

    private static final int COMMAND_MIN_PARTS = 1;

    private static final int COMMAND_MAX_PARTS = 2;

    @Override
    public List<Document> convert(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);
        String commandBody = GetBodyFromCommand.get(command);

        List<Document> deleteParts = ConvertCommandBody.toDocumentList(commandBody);

        //continue

        Document query = convertToDocument(commandBody);

        Document removeCommand =
                CommandBuilder
                        .remove(collectionName)
                            .query(query)
                        .extraParameters()
                            .none()
                        .build();

        return Arrays.asList(removeCommand);
    }

    private Document convertToDocument(String json) {

        return Document.parse(json);
    }

}
