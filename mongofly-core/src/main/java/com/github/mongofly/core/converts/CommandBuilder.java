package com.github.mongofly.core.converts;

import com.github.mongofly.core.domains.CommandType;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static final String INSERT_DOCUMENTS = "documents";

    private static final String UPDATE_DOCUMENTS = "updates";

    private static final String REMOVE_DOCUMENTS = "deletes";

    private static final String EMPTY = "EMPTY";

    private CommandType commandType;

    private String collectionName;

    private final List<Document> documents;

    private Boolean ordered = true;

    private String writeConcern;

    private InsertBuilder insertBuilder;

    private UpdateBuilder updateBuilder;

    private RemoveBuilder removeBuilder;

    private ExtraParametersBuilder extraParametersBuilder;

    private boolean extraParameters;

    private CommandBuilder(CommandType commandType, String collectionName) {
        this.commandType = commandType;
        this.collectionName = collectionName;
        this.documents = new ArrayList();
        this.writeConcern = EMPTY;
        this.extraParameters = false;

        insertBuilder = new InsertBuilder(this);
        updateBuilder = new UpdateBuilder(this);
        removeBuilder = new RemoveBuilder(this);

        extraParametersBuilder = new ExtraParametersBuilder(this);
    }

    public static InsertBuilder insert(String collectionName) {
        CommandBuilder commandBuilder = new CommandBuilder(CommandType.INSERT, collectionName);
        return commandBuilder.insertBuilder;
    }

    public static UpdateBuilder update(String collectionName) {
        CommandBuilder commandBuilder = new CommandBuilder(CommandType.UPDATE, collectionName);
        return commandBuilder.updateBuilder;
    }

    public static RemoveBuilder remove(String collectionName) {
        CommandBuilder commandBuilder = new CommandBuilder(CommandType.REMOVE, collectionName);
        return commandBuilder.removeBuilder;
    }

    public DBObject build() {

        BasicDBObject dbObject = new BasicDBObject();

        switch (commandType) {
            case INSERT:
                dbObject = insertBuilder.build();
                break;
            case UPDATE:
                dbObject = updateBuilder.build();
                break;
            case REMOVE:
                dbObject = removeBuilder.build();
                break;
        }

        if (extraParameters) {

            dbObject.append(CommandConvert.ORDERED, ordered);

            if (!EMPTY.equals(writeConcern)) {
                dbObject.append(CommandConvert.WRITE_CONVERN, writeConcern);
            }

        }

        return dbObject;
    }

    public class InsertBuilder {

        private CommandBuilder parentBuilder;

        private InsertBuilder(CommandBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        public InsertBuilder addManyDocument(List<Document> documents) {
            this.parentBuilder.documents.addAll(documents);
            return this;
        }

        public ExtraParametersBuilder extraParameters() {
            return parentBuilder.extraParametersBuilder;
        }

        private BasicDBObject build() {

            if (documents.isEmpty()) {
                throw new RuntimeException("Is necessary add documents to insert.");
            }

            BasicDBObject dbObject = new BasicDBObject();

            dbObject.append(CommandType.INSERT.getValue(), collectionName);
            dbObject.append(INSERT_DOCUMENTS, documents);

            return dbObject;
        }
    }

    public class UpdateBuilder {

        private final Document EMPTY_DOC = new Document();
        private Document query;
        private Document update;
        private Boolean multi = false;

        private CommandBuilder parentBuilder;

        private UpdateBuilder(CommandBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;

            this.query = EMPTY_DOC;
            this.update = EMPTY_DOC;
        }

        public UpdateBuilder query(Document query) {
            this.query = query;
            return this;
        }

        public UpdateBuilder update(Document update) {
            this.update = update;
            return this;
        }

        public UpdateBuilder multi(Optional<Boolean> multi) {
            if (multi.isPresent()) {
                this.multi = multi.get();
            }
            return this;
        }

        public ExtraParametersBuilder extraParameters() {
            return parentBuilder.extraParametersBuilder;
        }

        private BasicDBObject build() {

            if (EMPTY_DOC.equals(query) || EMPTY_DOC.equals(update)) {
                throw new RuntimeException("Is necessary add query and update in the command.");
            }

            BasicDBObject dbObject = new BasicDBObject();
            dbObject.append(CommandType.UPDATE.getValue(), collectionName);

            Document updateRow =  new Document();
            updateRow.append("q", query);
            updateRow.append("u", update);
            updateRow.append("multi", multi);

            documents.add(updateRow);
            dbObject.append(UPDATE_DOCUMENTS, documents);

            return dbObject;
        }

    }

    public class RemoveBuilder {

        private Document query;
        private int limit;

        private CommandBuilder parentBuilder;

        private RemoveBuilder(CommandBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;

            this.query = null;
            this.limit = 0;
        }

        public RemoveBuilder query(Document query) {
            this.query = query;
            return this;
        }

        public RemoveBuilder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public ExtraParametersBuilder extraParameters() {
            return parentBuilder.extraParametersBuilder;
        }

        private BasicDBObject build() {

            if (query == null) {
                throw new RuntimeException("Is necessary add query in the command. If command don't has one, add a new Document.");
            }

            BasicDBObject dbObject = new BasicDBObject();
            dbObject.append(CommandType.REMOVE.getValue(), collectionName);

            Document removeRow =  new Document();
            removeRow.append("q", query);
            removeRow.append("limit", limit);

            documents.add(removeRow);
            dbObject.append(REMOVE_DOCUMENTS, documents);

            return dbObject;
        }

    }

    public class ExtraParametersBuilder {

        private CommandBuilder parentBuilder;

        public ExtraParametersBuilder(CommandBuilder parentBuilder) {
            this.parentBuilder = parentBuilder;
        }

        public ExtraParametersBuilder ordered(Optional<Boolean> ordered) {
            if (ordered.isPresent()) {
                this.parentBuilder.ordered = ordered.get();
            }
            return this;
        }

        public ExtraParametersBuilder writeConcern(Optional<String> writeConcern) {
            if (writeConcern.isPresent()) {
                this.parentBuilder.writeConcern = writeConcern.get();
            }
            return this;
        }

        public CommandBuilder done() {

            if (this.parentBuilder.ordered != null ||
                    !this.parentBuilder.writeConcern.equals(EMPTY)) {
                this.parentBuilder.extraParameters = true;
            }

            return parentBuilder;
        }

        public CommandBuilder none() {
            this.parentBuilder.extraParameters = false;
            return parentBuilder;
        }

    }

//    CommandBuilder.insert("users").addManyDocuments(new List()).extraParameters().order(true).done().build();
//    CommandBuilder.insert("users").addManyDocuments(new List()).extraParameters().none().build();
//
//    CommandBuilder.update("users")
//                .query(new Document())
//                .update(new Document())
//                .multi(true)
//            .extraParameters()
//                .order(true)
//                .done()
//            .build();
}
