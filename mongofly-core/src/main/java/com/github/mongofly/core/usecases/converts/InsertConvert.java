package com.github.mongofly.core.usecases.converts;

import com.github.mongofly.core.utils.GetBodyFromCommand;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.*;

//TODO: utilizar constantes no lugar das strings
class InsertConvert implements CommandConvert {

    @Override
    public DBObject convert(String command) {

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

        //TODO: quebrar em lista de retorno, erro dado: "exceeded maximum write batch size of 1000"
        //não pode ter mais de 1000 elementos na lista de documentos
        CommandBuilder commandBuilder = CommandBuilder.insert(collectionName);
        commandBuilder.addManyDocument(documents);

        Document operation = operationParameters.orElse(new Document());
        if (operation.containsKey("ordered")) {
            commandBuilder.ordered(operation.getBoolean("ordered"));
        }

        if (operation.containsKey("writeConcern")) {
            commandBuilder.writeConcern(operation.getString("writeConcern"));
        }

        return commandBuilder.build();
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
}
