package com.github.mongofly.core.utils;

public class GetBodyFromCommand {

    private static final String PARENTHESES_OPEN = "(";
    private static final String PARENTHESES_CLOSE = ")";

    /**
     * Returns the body of a valid command,
     *
     * Samples:
     * <pre>
     * * db.user.find({name: "John Doe", age: 10})                         -> returns: {name: "John Doe", age: 10}
     * * db.otherCollection.update([{name: "John Doe", age: 10}, {...}])   -> returns: [{name: "John Doe", age: 10}, {...}]
     * * db.otherCollection.update([{...}, {...}],{...})                   -> returns: [{...}, {...}],{...}
     * </pre>
     *
     * @param command - a valid mongodb shell command
     * @return the body of command
     */
    public static String get(String command) {

        int firstParenthesesOpenPosition = command.indexOf(PARENTHESES_OPEN);
        int lastParenthesesClosePosition = command.lastIndexOf(PARENTHESES_CLOSE);
        String commandBody = command.substring(firstParenthesesOpenPosition + 1, lastParenthesesClosePosition);

        return commandBody;
    }
}
