package com.github.mongofly.core.commands.utils;

import com.github.mongofly.core.domains.CommandType;
import org.apache.commons.lang3.StringUtils;

public class GetCommandType {

    private static final String COMMAND_PREFIX = "db.";
    private static final String DOT = ".";
    private static final String PARENTHESES_OPEN = "(";

    public static CommandType fromCommand(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);

        String suffixBeforeCommandType = COMMAND_PREFIX + collectionName + DOT;
        String commandType = StringUtils.substringBetween(command, suffixBeforeCommandType, PARENTHESES_OPEN);

        return CommandType.fromValue(commandType);
    }

}
