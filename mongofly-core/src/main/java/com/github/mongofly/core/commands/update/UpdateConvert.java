package com.github.mongofly.core.commands.update;

import com.github.mongofly.core.commands.ConvertCommandBody;
import com.github.mongofly.core.commands.GetCommandType;
import com.github.mongofly.core.domains.CommandType;
import com.github.mongofly.core.utils.GetBodyFromCommand;
import com.github.mongofly.core.utils.GetCollation;
import com.github.mongofly.core.utils.GetWriteConcern;
import com.github.mongofly.core.utils.MongoflyException;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.List;
import java.util.Optional;

import static com.github.mongofly.core.converts.CommandConvert.MULTI;

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
public class UpdateConvert {

    private static final int COMMAND_MIN_PARTS = 2;

    private static final int COMMAND_MAX_PARTS = 3;

    private static final int COMMAND_QUERY_POSITION = 0;

    private static final int COMMAND_UPDATE_POSITION = 1;

    private static final int COMMAND_OPERATION_PARAMETERS_POSITION = 2;

    public static UpdateObject convert(String command) {

        String commandBody = GetBodyFromCommand.get(command);

        List<Document> updateParts = ConvertCommandBody.toDocumentList(commandBody);

        if(updateParts.size() < COMMAND_MIN_PARTS || updateParts.size() > COMMAND_MAX_PARTS) {
            throw new MongoflyException("Bad bson exception. There are problems with the sintaxe: ..." + command);
        }

        Document query = updateParts.get(COMMAND_QUERY_POSITION);
        Document update = updateParts.get(COMMAND_UPDATE_POSITION);

        Optional<UpdateOptions> updateOptions = Optional.empty();
        Optional<WriteConcern> writeConcern = Optional.empty();

        if(updateParts.size() == COMMAND_MAX_PARTS) {

            Document options = updateParts.get(COMMAND_OPERATION_PARAMETERS_POSITION);

            updateOptions = convertOptions(options);

            writeConcern = GetWriteConcern.get(options);
        }

        Boolean multi = isMulti(command, updateParts);

        return new UpdateObject(query, update, updateOptions, writeConcern, multi);
    }

    private static Optional<UpdateOptions> convertOptions(Document options) {

        if(!isOptionsParameters(options)) {

            return Optional.empty();
        }

        UpdateOptions updateOptions = new UpdateOptions();

        if (options.containsKey("upsert")) {
            updateOptions.upsert(options.getBoolean("upsert"));
        }

        if(options.containsKey("collation")) {
            updateOptions.collation(GetCollation.get(options).orElse(Collation.builder().build()));
        }

        if(options.containsKey("arrayFilters")) {
            updateOptions.arrayFilters(options.get("arrayFilters", List.class));
        }

        return Optional.of(updateOptions);

    }

    private static boolean isOptionsParameters(Document options) {

        return options.containsKey("upset") || options.containsKey("collation")  ||
                options.containsKey("arrayFilters") ;
    }

    private static Boolean isMulti(String command, List<Document> updateParts) {

        if(CommandType.UPDATE.equals(GetCommandType.fromCommand(command))) {

            if (updateParts.size() < COMMAND_MAX_PARTS) {
                return Boolean.FALSE;
            }

            Document options = updateParts.get(COMMAND_OPERATION_PARAMETERS_POSITION);

            if(options.containsKey(MULTI)) {

                return options.getBoolean(MULTI);
            }

            return Boolean.FALSE;
        }

        if(CommandType.INSERT_MANY.equals(GetCommandType.fromCommand(command))) {

            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }

}
