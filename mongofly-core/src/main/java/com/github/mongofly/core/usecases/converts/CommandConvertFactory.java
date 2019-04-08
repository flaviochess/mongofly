package com.github.mongofly.core.usecases.converts;

import com.github.mongofly.core.domains.CommandType;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

public class CommandConvertFactory {

    private static final String COMMAND_PREFIX = "db.";
    private static final String DOT = ".";
    private static final String PARENTHESES_OPEN = "(";

    private static final String REMOVE_TYPE = "remove";

    public CommandConvert factory(String command) {

        CommandType commandType = getCommandType(command);

        switch (commandType) {

            case INSERT:
                return new InsertConvert();
            case UPDATE:
            case DELETE:
            default:
                throw new NotImplementedException("Not implemented yet");
        }

    }

    private CommandType getCommandType(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);

        String suffixBeforeCommandType = COMMAND_PREFIX + collectionName + DOT;
        String commandType = StringUtils.substringBetween(command, suffixBeforeCommandType, PARENTHESES_OPEN);

        if (REMOVE_TYPE.equals(commandType)) {
            return CommandType.DELETE;
        }

        return CommandType.valueOf(commandType.toUpperCase());
    }

}
