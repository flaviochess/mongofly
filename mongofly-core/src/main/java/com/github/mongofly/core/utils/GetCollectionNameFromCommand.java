package com.github.mongofly.core.utils;

public class GetCollectionNameFromCommand {

    private static final String DOT_REGEX = "\\.";
    private static final int SPLIT_LIMIT = 3;
    private static final int COLLECTION_NAME_POSITION = 1;
    /**
     * Returns the collection name from a valid command,
     *
     * Samples:
     * <pre>
     * * db.user.find({})                   ->  user
     * * db.otherCollection.update({...})   ->  otherCollection
     * </pre>
     *
     * @param command - a valid mongodb shell command
     * @return the collection name
     */
    public static String get(String command) {

        return command.split(DOT_REGEX, SPLIT_LIMIT)[COLLECTION_NAME_POSITION];
    }
}
