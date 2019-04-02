package com.github.mongofly.core.usecases;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringStack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Slf4j
@Component
public class RunMongoCommand {

    private static final String COMMAND_PREFIX = "db.";
    private static final String DOT = "\\.";
    private static final String INSERT = "insert";
    private static final String PARENTHESES_OPEN = "(";
    private static final String PARENTHESES_CLOSE = ")";
    private static final String COMMAND_SUFIX = PARENTHESES_CLOSE + ";";

    public void run(String command) {

        log.debug(command);

        if (!command.startsWith(COMMAND_PREFIX)) {
            throw new RuntimeException();
        }

        String collectionName = command.split("\\.", 3)[1];

        String sufixBeforeCommandType = COMMAND_PREFIX + collectionName + ".";
        String commandType = StringUtils.substringBetween(command, sufixBeforeCommandType, PARENTHESES_OPEN);

        int firstParenthesesOpenPosition = command.indexOf(PARENTHESES_OPEN);
        int lastParenthesesClosePosition = command.lastIndexOf(PARENTHESES_CLOSE);
        String commandBody = command.substring(firstParenthesesOpenPosition + 1, lastParenthesesClosePosition);

        if (INSERT.equals(commandType)) {

            insert(collectionName, commandBody);
        }

        //da para depois fazer uma factory para chamar uma classe especilista de acordo com o tipo de comando
    }

}
