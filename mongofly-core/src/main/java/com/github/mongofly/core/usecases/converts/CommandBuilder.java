package com.github.mongofly.core.usecases.converts;

import com.github.mongofly.core.domains.CommandType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Build a mongodb operation command.
 * Only for insert, update or delete.
 * <p>
 * Sample, a insert command:
 * <pre>
 *     {
 *        insert: <collection>,
 *        documents: [ <document>, <document>, <document>, ... ],
 *        ordered: <boolean>,
 *        writeConcern: { <write concern> }
 *     }
 * </pre>
 *
 * @see <a href="https://docs.mongodb.com/manual/reference/command/nav-crud/">https://docs.mongodb.com/manual/reference/command/nav-crud/</a>
 */
public class CommandBuilder {

    private static final String EMPTY = "EMPTY";

    private CommandType commandType;

    private String collectionName;

    private List<Document> documents;

    private Boolean ordered;

    private String writeConcern;

    private CommandBuilder(CommandType commandType, String collectionName) {
        this.commandType = commandType;
        this.collectionName = collectionName;
        this.documents = new ArrayList();
        this.writeConcern = EMPTY;
    }

    public static CommandBuilder insert(String collectionName) {
        return new CommandBuilder(CommandType.INSERT, collectionName);
    }

    public static CommandBuilder update(String collectionName) {
        return new CommandBuilder(CommandType.UPDATE, collectionName);
    }

    public static CommandBuilder delete(String collectionName) {
        return new CommandBuilder(CommandType.DELETE, collectionName);
    }

    public CommandBuilder addDocument(Document document) {
        this.documents.add(document);
        return this;
    }

    public CommandBuilder addManyDocument(List<Document> documents) {
        this.documents.addAll(documents);
        return this;
    }

    public CommandBuilder ordered(boolean ordered) {
        this.ordered = ordered;
        return this;
    }

    public CommandBuilder writeConcern(String writeConcern) {
        this.writeConcern = writeConcern;
        return this;
    }

    public DBObject build() {

        if (documents.isEmpty()) {
            throw new RuntimeException();
        }

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append(commandType.getValue(), collectionName);

        switch (commandType) {
            case INSERT:
                dbObject.append("documents", documents);
                break;
            case UPDATE:
                dbObject.append("updates", documents);
                break;
            case DELETE:
                dbObject.append("deletes", documents);
                break;
        }

        if (ordered != null) {
            dbObject.append("ordered", ordered);
        }

        if (!EMPTY.equals(writeConcern)) {
            dbObject.append("writeConcern", writeConcern);
        }

        return dbObject;
    }

}
