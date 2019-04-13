package com.github.mongofly.core.converts;

import com.github.mongofly.core.utils.GetBodyFromCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.github.mongofly.core.utils.MongoflyException;
import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;

/*
    TODO:
    Possibilities

        * db.collection.insert({param1: value1, param2: value2, ...});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}]);
        * db.collection.insert({param1: value1, param2: value2}, {writeConcern: false, ordered: true});
        * db.collection.insert([{param1: value1, param2: value2}, {param1: value1, param2: value2}], {writeConcern: false, ordered: true});

     */


class InsertConvert implements CommandConvert {

    @Override
    public List<DBObject> convert(String command) {

        String collectionName = GetCollectionNameFromCommand.get(command);
        String commandBody = GetBodyFromCommand.get(command);

        List<Document> documents;
        Optional<Document> operationParameters = Optional.empty();

        if (isSimpleCommand(commandBody)) {

            Document simpleDocument = convertToDocument(commandBody);
            documents = Arrays.asList(simpleDocument);

        } else {

            documents = convertToDocumentList(commandBody);

            if (documents.size() > 1) {
                Document lastDocument = documents.get(documents.size() - 1);

                if (lastDocument.containsKey(ORDERED) ||
                        lastDocument.containsKey(WRITE_CONVERN)) {

                    operationParameters = Optional.of(lastDocument);
                    documents.remove(documents.size() - 1);
                }
            }

        }

        List<List<Document>> partitionedDocuments = partitionDocuments(documents);
        Document operation = operationParameters.orElse(new Document());

        List<DBObject> inserts = partitionedDocuments.stream()
                .map(docs -> this.buildDBObject(collectionName, docs, operation))
                .collect(Collectors.toList());

        return inserts;
    }

    private boolean isSimpleCommand(String commandBody) {

        return StringUtils.countMatches(commandBody, OPEN_CURLY_BRACES) == 1 &&
                StringUtils.countMatches(commandBody, CLOSE_CURLY_BRACES) == 1;
    }

    private List<Document> convertToDocumentList(String commandBody) {

        List<Document> documents = new ArrayList();

        Stack<Character> bracesStack = new Stack();
        int firstBraceOfJson = 0;

        for (int positionOfLetter = 0; positionOfLetter < commandBody.length(); positionOfLetter++) {

            if (commandBody.charAt(positionOfLetter) == OPEN_CURLY_BRACES) {

                bracesStack.push(commandBody.charAt(positionOfLetter));

                if (firstBraceOfJson == 0) {
                    firstBraceOfJson = positionOfLetter;
                }

                continue;
            }

            if (commandBody.charAt(positionOfLetter) == CLOSE_CURLY_BRACES) {

                bracesStack.pop();

                if (bracesStack.isEmpty()) {

                    String commandBodyJson = commandBody.substring(firstBraceOfJson, positionOfLetter + 1);
                    documents.add(convertToDocument(commandBodyJson));
                    firstBraceOfJson = 0;
                }
            }

        }

        if (!bracesStack.isEmpty()) {
            throw new MongoflyException("Bad bson exception. There are \"{\" without closing: " + commandBody);
        }

        return documents;

    }

    private Document convertToDocument(String commandBody) {

        return Document.parse(commandBody);
    }

    private List<List<Document>> partitionDocuments(List<Document> documents) {

        return Lists.partition(documents, DOCUMENTS_LIMIT_SIZE);
    }

    private DBObject buildDBObject(String collectionName, List<Document> documents, Document operationParameters) {

        CommandBuilder commandBuilder = CommandBuilder.insert(collectionName).addManyDocument(documents);

        if (operationParameters.containsKey(ORDERED)) {
            commandBuilder.ordered(operationParameters.getBoolean(ORDERED));
        }

        if (operationParameters.containsKey(WRITE_CONVERN)) {
            commandBuilder.writeConcern(operationParameters.getString(WRITE_CONVERN));
        }

        return commandBuilder.build();
    }
}
