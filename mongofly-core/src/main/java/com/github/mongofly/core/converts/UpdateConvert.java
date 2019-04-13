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

    @Override
    public List<DBObject> convert(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);
        String commandBody = GetBodyFromCommand.get(command);

        List<Document> documents;
        Optional<Document> operationParameters = Optional.empty();

        String[] updateParts = commandBody.split("},");

        if(updateParts.length < 2 || updateParts.length > 3) {
            throw new MongoflyException("Bad bson exception. There are problems with the sintaxe: ..." + commandBody);
        }

        Document query = convertToDocument(updateParts[0] + "}");
        Document update = convertToDocument(updateParts[1] + "}");

        if(updateParts.length == 3) {
            operationParameters = Optional.of(convertToDocument(updateParts[2]));
        }

        Document operation = operationParameters.orElse(new Document());

        DBObject dbObjectUpdate = buildDBObject(collectionName, query, update, operation);

        return Arrays.asList(dbObjectUpdate);
    }

    private Document convertToDocument(String json) {

        return Document.parse(json);
    }

    private DBObject buildDBObject(String collectionName, Document query, Document update, Document operationParameters) {

        UpdateBuilder commandBuilder = UpdateBuilder.update(collectionName).query(query).update(update);

        if (operationParameters.containsKey(WRITE_CONVERN)) {
            commandBuilder.writeConcern(operationParameters.getString(WRITE_CONVERN));
        }

        if (operationParameters.containsKey(MULTI)) {
            commandBuilder.multi(operationParameters.getBoolean(MULTI));
        }

        if (operationParameters.containsKey(ORDERED)) {
            commandBuilder.ordered(operationParameters.getBoolean(ORDERED));
        }

        return commandBuilder.build();
    }

}
