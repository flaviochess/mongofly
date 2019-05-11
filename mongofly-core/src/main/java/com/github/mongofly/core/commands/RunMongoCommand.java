package com.github.mongofly.core.commands;

import com.github.mongofly.core.converts.CommandConvertFactory;
import com.github.mongofly.core.utils.MongoflyException;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class RunMongoCommand {

    private static final String COMMAND_PREFIX = "db.";

    private CommandConvertFactory commandConvertFactory;

    private MongoTemplate mongoTemplate;

    @Autowired
    public RunMongoCommand(CommandConvertFactory commandConvertFactory, MongoTemplate mongoTemplate) {
        this.commandConvertFactory = commandConvertFactory;
        this.mongoTemplate = mongoTemplate;
    }

    public void run(String command) {

        log.debug("Trying to execute: " + command);

        if (!command.startsWith(COMMAND_PREFIX)) {
            throw new MongoflyException("It's not a valid command. This does not start with \"db...\": " + command);
        }

        mongoTemplate.getDb().getCollection("").insertMany();

        List<Document> convertedCommands = commandConvertFactory.factory(command).convert(command);

        List<Document> commandResults = new ArrayList();

        try {

            convertedCommands.forEach(convertedCommand -> {

                Document commandResult = mongoTemplate.executeCommand(convertedCommand);

                if (isNotOK(commandResult)) {

                    log.debug("An error occurred while attempting to execute the command: " + convertedCommand);
                    String errorMessage = "An error occurred while executing: ";

                    if(commandResult.containsKey("writeErrors")) {
                        errorMessage += ((List<Document>) commandResult.get("writeErrors")).get(0).toJson();
                    } else {
                        errorMessage += "Mongo returned not OK in command: " + command;
                    }

                    throw new MongoflyException(errorMessage);
                }

                commandResults.add(commandResult);
            });

        } catch (RuntimeException ex) {

            log.error("Error trying to execute the command: " + command);
            throw ex;
        }

        long totalInstructions = commandResults.stream().mapToLong(this::getNCompatibleWithAllMongoVersions).sum();
        long totalModified = commandResults.stream().mapToLong(this::getNModifiedCompatibleWithAllMongoVersions).sum();

        log.debug("Number of documents selected" + totalInstructions);
        log.debug("Number of documents selected" + totalModified);
    }

    private boolean isNotOK(Document commandResult) {

        Double ok = getOKCompatibleWithAllMongoVersions(commandResult);

        return ok < 1 || commandResult.containsKey("writeErrors");
    }

    private Double getOKCompatibleWithAllMongoVersions(Document commandResult) {

        return Double.valueOf(commandResult.get("ok").toString());
    }

    private long getNCompatibleWithAllMongoVersions(Document commandResult) {

        return Long.valueOf(commandResult.get("n").toString());
    }

    private long getNModifiedCompatibleWithAllMongoVersions(Document commandResult) {

        Object nModified = commandResult.getOrDefault("nModified", 0);

        return Long.valueOf(nModified.toString());
    }
}
