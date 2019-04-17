package com.github.mongofly.core.converts;

import com.github.mongofly.core.utils.GetBodyFromCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.github.mongofly.core.utils.MongoflyException;
import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
    Samples:

    db.runCommand(
       {
          update: "city",
          updates: [
             { q: {"postalAbbreviation": "AC", "type" : "CITY"}, u: {$addToSet: { "parentRegions" : {$each: ["state-acre"]} } }, multi: true }
          ]
       }
    )

    db.runCommand(
       {
          update: "users",
          updates: [
             {
               q: { user: "abc123" }, u: { $set: { status: "A" }, $inc: { points: 1 } }
             }
          ],
          ordered: false,
          writeConcern: { w: "majority", wtimeout: 5000 }
       }
    )

    from:

    db.city.update(
        {"postalAbbreviation": "AC", "type" : "CITY"},
        {$addToSet: { "parentRegions" : {$each: ["state-acre"]} }},
        {multi: true}
    );

    db.books.update(
       { item: "EFG222" },
       { $set: { reorder: false, tags: [ "literature", "translated" ] } },
       { upsert: true, multi: true }
    );

 */
public class UpdateConvert implements CommandConvert {

    private static final String CURLY_BRACES_CLOSE_COMMA = "},";

    private static final int COMMAND_MIN_PARTS = 2;

    private static final int COMMAND_MAX_PARTS = 3;

    private static final int COMMAND_QUERY_POSITION = 0;

    private static final int COMMAND_UPDATE_POSITION = 1;

    private static final int COMMAND_OPERATION_PARAMETERS_POSITION = 2;

    @Override
    public List<DBObject> convert(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);
        String commandBody = GetBodyFromCommand.get(command);

        Optional<Document> operationParameters = Optional.empty();

        String[] updateParts = commandBody.split(CURLY_BRACES_CLOSE_COMMA);

        if(updateParts.length < COMMAND_MIN_PARTS || updateParts.length > COMMAND_MAX_PARTS) {
            throw new MongoflyException("Bad bson exception. There are problems with the sintaxe: ..." + commandBody);
        }

        Document query = convertToDocument(updateParts[COMMAND_QUERY_POSITION] + CURLY_BRACES_CLOSE);
        Document update = convertToDocument(updateParts[COMMAND_UPDATE_POSITION] + CURLY_BRACES_CLOSE);

        if(updateParts.length == COMMAND_MAX_PARTS) {
            operationParameters = Optional.of(convertToDocument(updateParts[COMMAND_OPERATION_PARAMETERS_POSITION]));
        }

        Document operation = operationParameters.orElse(new Document());

        DBObject dbObjectUpdate = buildDBObject(collectionName, query, update, operation);

        return Arrays.asList(dbObjectUpdate);
    }

    private Document convertToDocument(String json) {

        return Document.parse(json);
    }

    private DBObject buildDBObject(String collectionName, Document query, Document update, Document operationParameters) {

        Optional<String> writeConcern = operationParameters.containsKey(WRITE_CONVERN)?
                Optional.of(operationParameters.getString(WRITE_CONVERN)) : Optional.empty();

        Optional<Boolean> multi = operationParameters.containsKey(MULTI) ?
                Optional.of(operationParameters.getBoolean(MULTI)) : Optional.empty();

        return CommandBuilder
                .update(collectionName)
                    .query(query)
                    .update(update)
                    .multi(multi)
                .extraParameters()
                    .writeConcern(writeConcern)
                    .done()
                .build();
    }

}
