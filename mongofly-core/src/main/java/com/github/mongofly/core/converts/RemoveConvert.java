package com.github.mongofly.core.converts;

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

    @Override
    public List<DBObject> convert(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);
        String commandBody = GetBodyFromCommand.get(command);

        Document query = convertToDocument(commandBody);

        DBObject dbObjectRemove =
                CommandBuilder
                        .remove(collectionName)
                            .query(query)
                        .extraParameters()
                            .none()
                        .build();

        return Arrays.asList(dbObjectRemove);
    }

    private Document convertToDocument(String json) {

        return Document.parse(json);
    }

}
