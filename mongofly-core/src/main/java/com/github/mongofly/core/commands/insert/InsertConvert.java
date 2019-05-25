package com.github.mongofly.core.commands.insert;

import com.github.mongofly.core.commands.utils.ConvertCommandBody;
import com.github.mongofly.core.commands.utils.GetBodyFromCommand;
import com.github.mongofly.core.commands.utils.GetWriteConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.model.InsertManyOptions;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.mongofly.core.commands.utils.ConvertCommandBody.CURLY_BRACES_CLOSE;
import static com.github.mongofly.core.commands.utils.ConvertCommandBody.CURLY_BRACES_OPEN;
import static com.github.mongofly.core.commands.utils.GetWriteConcern.WRITE_CONCERN;

/*
    TODO:
    Possibilities

        * db.collection.insert({param1: value1, param2: value2, ...});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}]);
        * db.collection.insert({param1: value1, param2: value2}, {writeConcern: false, ordered: true});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}], {writeConcern: false, ordered: true});

     */

public class InsertConvert {

    private static final String ORDERED = "ordered";

    public static InsertObject convert(String command) {

        String commandBody = GetBodyFromCommand.get(command);

        List<Document> documents;
        Optional<InsertManyOptions> insertManyOptions = Optional.empty();
        Optional<WriteConcern> writeConcern = Optional.empty();

        if (isSimpleCommand(commandBody)) {

            Document simpleDocument = convertToDocument(commandBody);
            documents = Arrays.asList(simpleDocument);

        } else {

            documents = ConvertCommandBody.toDocumentList(commandBody);

            if (documents.size() > 1) {
                Document lastDocument = documents.get(documents.size() - 1);

                if (isOptionsParameters(lastDocument)) {

                    insertManyOptions = convertOptions(lastDocument);

                    writeConcern = GetWriteConcern.get(lastDocument);

                    documents.remove(documents.size() - 1);
                }
            }

        }

        return new InsertObject(documents, insertManyOptions, writeConcern);
    }

    private static boolean isOptionsParameters(Document lastDocument) {
        return lastDocument.containsKey(ORDERED) ||
                lastDocument.containsKey(WRITE_CONCERN);
    }

    private static boolean isSimpleCommand(String commandBody) {

        return StringUtils.countMatches(commandBody, CURLY_BRACES_OPEN) == 1 &&
                StringUtils.countMatches(commandBody, CURLY_BRACES_CLOSE) == 1;
    }


    private static Document convertToDocument(String commandBody) {

        return Document.parse(commandBody);
    }

    private static Optional<InsertManyOptions> convertOptions(Document options) {

        if(!options.containsKey(ORDERED)) {
            return Optional.empty();
        }

        InsertManyOptions insertManyOptions = new InsertManyOptions();

        if(options.containsKey(ORDERED)) {

            Boolean ordered;

            try{
                ordered = options.getBoolean(ORDERED);

            } catch (ClassCastException cce) {

                Integer orderedInt = options.getInteger(ORDERED);
                ordered = orderedInt == 0? Boolean.FALSE : Boolean.TRUE;

            }

            insertManyOptions.ordered(ordered);

        }

        return Optional.of(insertManyOptions);
    }

}
