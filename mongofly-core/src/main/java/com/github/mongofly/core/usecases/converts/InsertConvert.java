package com.github.mongofly.core.usecases.converts;

import com.github.mongofly.core.utils.GetBodyFromCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.google.common.collect.Lists;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

//TODO: utilizar constantes no lugar das strings
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

                if (lastDocument.containsKey("ordered") ||
                        lastDocument.containsKey("writeConcern")) {

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

        return StringUtils.countMatches(commandBody, '{') == 1 &&
                StringUtils.countMatches(commandBody, '}') == 1;
    }

    private List<Document> convertToDocumentList(String commandBody) {

        List<Document> documents = new ArrayList();

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
                    documents.add(convertToDocument(commandBodyJson));
                    firstBraceOfJson = 0;
                }
            }

        }

        if (!bracesStack.isEmpty()) {
            throw new RuntimeException();
        }

        return documents;

    }

    private Document convertToDocument(String commandBody) {

        return Document.parse(commandBody);
    }

    private List<List<Document>> partitionDocuments(List<Document> documents) {

        BigDecimal documentsSize = new BigDecimal(documents.size());
        BigDecimal limitSize = new BigDecimal(DOCUMENTS_LIMIT_SIZE);

        int partitionsNumber = documentsSize.divide(limitSize).setScale(0, RoundingMode.UP).intValue();

        return Lists.partition(documents, partitionsNumber);
    }

    private DBObject buildDBObject(String collectionName, List<Document> documents, Document operationParameters) {

        CommandBuilder commandBuilder = CommandBuilder.insert(collectionName).addManyDocument(documents);

        if (operationParameters.containsKey("ordered")) {
            commandBuilder.ordered(operationParameters.getBoolean("ordered"));
        }

        if (operationParameters.containsKey("writeConcern")) {
            commandBuilder.writeConcern(operationParameters.getString("writeConcern"));
        }

        return commandBuilder.build();
    }
}
