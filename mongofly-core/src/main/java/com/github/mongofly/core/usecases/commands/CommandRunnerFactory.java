package com.github.mongofly.core.usecases.commands;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommandRunnerFactory {

    private static final String COMMAND_PREFIX = "db.";
    private static final String DOT = ".";
    private static final String PARENTHESES_OPEN = "(";

    private static final String INSERT = "insert";
    private static final String UPDATE = "update";

    public CommandRunner factory(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);

        String suffixBeforeCommandType = COMMAND_PREFIX + collectionName + DOT;
        String commandType = StringUtils.substringBetween(command, suffixBeforeCommandType, PARENTHESES_OPEN);

        switch (commandType) {

            case INSERT:
                return new InsertRunner();
            case UPDATE:
                throw new NotImplementedException("Not implemented yet");
            default:
                throw new RuntimeException();
        }

    }

}
