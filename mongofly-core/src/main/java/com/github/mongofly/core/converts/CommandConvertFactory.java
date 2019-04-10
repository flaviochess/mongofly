package com.github.mongofly.core.converts;

import com.github.mongofly.core.domains.CommandType;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.github.mongofly.core.utils.MongoflyException;
import org.apache.commons.lang3.StringUtils;

public class CommandConvertFactory {

    private static final String COMMAND_PREFIX = "db.";
    private static final String DOT = ".";
    private static final String PARENTHESES_OPEN = "(";

    public CommandConvert factory(String command) {

        CommandType commandType = getCommandType(command);

        switch (commandType) {

            case INSERT:
                return new InsertConvert();
            case UPDATE:
            case REMOVE:
            default:
                throw new MongoflyException("Not implemented yet");
        }

    }

    private CommandType getCommandType(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);

        String suffixBeforeCommandType = COMMAND_PREFIX + collectionName + DOT;
        String commandType = StringUtils.substringBetween(command, suffixBeforeCommandType, PARENTHESES_OPEN);

        return CommandType.valueOf(commandType.toUpperCase());
    }

}
