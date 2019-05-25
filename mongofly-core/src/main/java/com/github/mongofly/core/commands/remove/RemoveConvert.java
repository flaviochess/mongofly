package com.github.mongofly.core.commands.remove;

import com.github.mongofly.core.commands.utils.*;
import com.github.mongofly.core.domains.CommandType;
import com.github.mongofly.core.exceptions.MongoflyException;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.DeleteOptions;
import org.bson.Document;

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
public class RemoveConvert {

    private static final int COMMAND_MIN_PARTS = 1;

    private static final int COMMAND_MAX_PARTS = 2;

    private static final int COMMAND_QUERY_POSITION = 0;

    private static final int COMMAND_OPERATION_PARAMETERS_POSITION = 1;

    private static final String JUST_ONE = "justOne";

    public static DeleteObject convert(String command) {

        String commandBody = GetBodyFromCommand.get(command);

        List<Document> deleteParts = ConvertCommandBody.toDocumentList(commandBody);

        if(deleteParts.size() < COMMAND_MIN_PARTS || deleteParts.size() > COMMAND_MAX_PARTS) {
            throw new MongoflyException("Bad bson exception. There are problems with the sintaxe: ..." + command);
        }

        Document query = deleteParts.get(COMMAND_QUERY_POSITION);

        Optional<DeleteOptions> deleteOptions = Optional.empty();
        Optional<WriteConcern> writeConcern = Optional.empty();

        if(deleteParts.size() == COMMAND_MAX_PARTS) {

            Document options = deleteParts.get(COMMAND_OPERATION_PARAMETERS_POSITION);

            if(options.containsKey("collation")) {
                DeleteOptions opts = new DeleteOptions();
                opts.collation(GetCollation.get(options).orElse(Collation.builder().build()));
                deleteOptions = Optional.of(new DeleteOptions());
            }

            writeConcern = GetWriteConcern.get(options);
        }

        Boolean justOne = isJustOne(command, deleteParts);

        return new DeleteObject(query, deleteOptions, writeConcern, justOne);
    }


    private static Boolean isJustOne(String command, List<Document> deleteParts) {

        if(CommandType.REMOVE.equals(GetCommandType.fromCommand(command))) {

            if (deleteParts.size() < COMMAND_MAX_PARTS) {
                return Boolean.FALSE;
            }

            Document options = deleteParts.get(COMMAND_OPERATION_PARAMETERS_POSITION);

            if(options.containsKey(JUST_ONE)) {

                return options.getBoolean(JUST_ONE);
            }

            return Boolean.FALSE;
        }

        if(CommandType.DELETE_ONE.equals(GetCommandType.fromCommand(command))) {

            return Boolean.TRUE;
        }

        return Boolean.FALSE;

    }
}
