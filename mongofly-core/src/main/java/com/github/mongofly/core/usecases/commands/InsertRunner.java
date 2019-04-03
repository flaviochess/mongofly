package com.github.mongofly.core.usecases.commands;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

    /*
    TODO:
    Possibilities

        * db.collection.insert({param1: value1, param2: value2, ...});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}]);
        * db.collection.insert({param1: value1, param2: value2}, {writeConcern: false, ordered: true});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}], {writeConcern: false, ordered: true});

     */


class InsertRunner implements CommandRunner {

    @Autowired
    public void run(String collectionName, String commandBody) {

        commandBody = commandBody.trim();

        if (!isCommnadBodyValidStart(commandBody)) {
            throw new RuntimeException();
        }

        if (isSimpleCommand(commandBody)) {

            executeSimpleCommand(collectionName, commandBody);

        } else {

            executeComplexCommand(collectionName, commandBody);
        }

    }

    private void runInsert(String collectionName, String json) {

        System.out.println(collectionName + " - " + json);
    }

    private boolean isCommnadBodyValidStart(String commandBody) {

        return commandBody.startsWith("{") || commandBody.startsWith("[");
    }

    private boolean isSimpleCommand(String commandBody) {

        return StringUtils.countMatches(commandBody, '{') == 1;
    }

    private void executeSimpleCommand(String collectionName, String commandBody) {

        if (StringUtils.countMatches(commandBody, '}') != 1) {
            throw new RuntimeException();
        }

        runInsert(collectionName, commandBody);
    }

    private void executeComplexCommand(String collectionName, String commandBody) {

        List<String> commandBodyList = new ArrayList();

        Stack<Character> bracesStack = new Stack();
        int firstBraceOfJson = 0;

        for (int positionOfLetter = 0; positionOfLetter < commandBody.length(); positionOfLetter++) {

            if (commandBody.charAt(positionOfLetter) == '{') {

                bracesStack.push(commandBody.charAt(positionOfLetter));

                if (firstBraceOfJson == 0) {
                    firstBraceOfJson = positionOfLetter;
                }

                continue;
            }

            if (commandBody.charAt(positionOfLetter) == '}') {

                bracesStack.pop();

                if (bracesStack.isEmpty()) {

                    String commandBodyJson = commandBody.substring(firstBraceOfJson, positionOfLetter + 1);
                    commandBodyList.add(commandBodyJson);
                    firstBraceOfJson = 0;
                }
            }

        }

        if (!bracesStack.isEmpty()) {
            throw new RuntimeException();
        }

        String operationParameter = commandBodyList.get(commandBodyList.size() - 1);
        if (StringUtils.containsAny(operationParameter, "writeConcern", "ordered")) {

            commandBodyList.remove(commandBodyList.size() - 1);
            runMultipleCommands(collectionName, commandBodyList, operationParameter);

        } else {

            runMultipleCommands(collectionName, commandBodyList);
        }

    }

    private void runMultipleCommands(String collectionName, List<String> commandBodyList, String operationParameter) {

        throw new NotImplementedException("Not implemented yet");
    }

    private void runMultipleCommands(String collectionName, List<String> commandBodyList) {

        commandBodyList.forEach(commandBodyJson -> runInsert(collectionName, commandBodyJson));
    }
}
