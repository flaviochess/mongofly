package com.github.mongofly.core.converts;

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
public class UpdateBuilder {

    private static final String EMPTY = "EMPTY";

    private CommandType commandType;

    private String collectionName;

    private List<Document> documents;

    private Document query;

    private Document update;

    private Boolean ordered;

    private String writeConcern;

    private Boolean multi;

    private UpdateBuilder(CommandType commandType, String collectionName) {
        this.commandType = commandType;
        this.collectionName = collectionName;
        this.documents = new ArrayList();
        this.writeConcern = EMPTY;
    }

    public static UpdateBuilder update(String collectionName) {
        return new UpdateBuilder(CommandType.UPDATE, collectionName);
    }

    public UpdateBuilder query(Document query) {
        this.query = query;
        return this;
    }

    public UpdateBuilder update(Document update) {
        this.update = update;
        return this;
    }

    public UpdateBuilder ordered(boolean ordered) {
        this.ordered = ordered;
        return this;
    }

    public UpdateBuilder writeConcern(String writeConcern) {
        this.writeConcern = writeConcern;
        return this;
    }

    public UpdateBuilder multi(boolean multi) {
        this.multi = multi;
        return this;
    }

    public DBObject build() {

        BasicDBObject dbObject = new BasicDBObject();
        dbObject.append(commandType.getValue(), collectionName);

        Document updateRow =  new Document();
        updateRow.append("q", query);
        updateRow.append("u", update);

        if (multi != null) {
            updateRow.append("multi", multi);
        }

        documents.add(updateRow);

        switch (commandType) {
            case UPDATE:
                dbObject.append("updates", documents);
                break;
        }

        if (!EMPTY.equals(writeConcern)) {
            dbObject.append("writeConcern", writeConcern);
        }

        if (ordered != null) {
            dbObject.append("ordered", ordered);
        }

        return dbObject;
    }

}
