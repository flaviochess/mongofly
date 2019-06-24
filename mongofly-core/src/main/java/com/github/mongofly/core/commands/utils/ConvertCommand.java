package com.github.mongofly.core.commands.utils;

import com.github.mongofly.core.exceptions.MongoflyException;
import org.bson.Document;

import java.util.List;

public class ConvertCommand {

    public static List<Document> toCommandParts(String command, int commandMinParts, int commandMaxParts) {

        String commandBody = GetBodyFromCommand.get(command);

        List<Document> commandParts = ConvertCommandBody.toDocumentList(commandBody);

        if(commandParts.size() < commandMinParts || commandParts.size() > commandMaxParts) {
            throw new MongoflyException("Bad bson exception. There are problems with the sintaxe: ..." + command);
        }

        return commandParts;
    }

}
