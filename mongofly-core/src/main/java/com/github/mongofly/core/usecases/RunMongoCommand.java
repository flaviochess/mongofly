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

    /*
    TODO:
    Possibilities

        * db.collection.insert({param1: value1, param2: value2, ...});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}]);
        * db.collection.insert({param1: value1, param2: value2}, {writeConcern: false, ordered: true});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}], {writeConcern: false, ordered: true});

     */

    private void insert(String collectionName, String commandBody) {

        commandBody = commandBody.trim();

        if(!commandBody.startsWith("{") && !commandBody.startsWith("[")) {
            throw new RuntimeException();
        }

        if (StringUtils.countMatches(commandBody, '{') == 1) {

            if (StringUtils.countMatches(commandBody, '}') != 1) {
                throw new RuntimeException();
            }

            int beginOfJson = commandBody.indexOf('{');
            int finalOfJson = commandBody.indexOf('}');
            String commandBodyJson = commandBody.substring(beginOfJson, finalOfJson + 1);
            runInsert(collectionName, commandBodyJson);
            return;
        }

        //fazer todos esse role apenas se houver mais de um { na string

        Stack<Character> bracesStack = new Stack();
        int firstBraceOfJson = 0;

        for (int positionOfLetter = 0; positionOfLetter < commandBody.length(); positionOfLetter++) {

            if (commandBody.charAt(positionOfLetter) == '{') {
                bracesStack.push(commandBody.charAt(positionOfLetter));

                if(firstBraceOfJson == 0) {
                    firstBraceOfJson = positionOfLetter;
                }

                continue;
            }

            if(commandBody.charAt(positionOfLetter) == '}') {
                bracesStack.pop();

                if(bracesStack.isEmpty()) {
                    String commandBodyJson = commandBody.substring(firstBraceOfJson, positionOfLetter + 1);

                    //TODO: antes de executar verificar se é o último e se não é um caso de parametros de configuração

                    runInsert(collectionName, commandBodyJson);
                    firstBraceOfJson = 0;
                }
            }

        }

        if (!bracesStack.isEmpty()) {
            throw new RuntimeException();
        }
    }

    private void runInsert(String collectionName, String json) {

        System.out.println(collectionName + " - " + json);
    }

}
