package com.github.mongofly.core.usecases.commands;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Slf4j
@Component
public class RunMongoCommand {

    private static final String COMMAND_PREFIX = "db.";
    private static final String PARENTHESES_OPEN = "(";
    private static final String PARENTHESES_CLOSE = ")";

    private CommandRunnerFactory commandRunnerFactory;

    @Autowired
    public RunMongoCommand(CommandRunnerFactory commandRunnerFactory) {
        this.commandRunnerFactory = commandRunnerFactory;
    }

    public void run(String command) {

        log.debug(command);

        if (!command.startsWith(COMMAND_PREFIX)) {
            throw new RuntimeException();
        }

        String collectionName = GetCollectionNameFromCommand.get(command);

        int firstParenthesesOpenPosition = command.indexOf(PARENTHESES_OPEN);
        int lastParenthesesClosePosition = command.lastIndexOf(PARENTHESES_CLOSE);
        String commandBody = command.substring(firstParenthesesOpenPosition + 1, lastParenthesesClosePosition);

        commandRunnerFactory.factory(command).run(collectionName, commandBody);
    }

}
