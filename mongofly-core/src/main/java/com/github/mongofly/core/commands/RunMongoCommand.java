package com.github.mongofly.core.commands;

import com.github.mongofly.core.converts.CommandConvertFactory;
import com.github.mongofly.core.utils.MongoflyException;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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

        List<Document> convertedCommands = commandConvertFactory.factory(command).convert(command);

        List<Document> commandResults = new ArrayList();

        try {

            convertedCommands.forEach(convertedCommand -> {

                Document commandResult = mongoTemplate.executeCommand(convertedCommand);
                if (commandResult.getDouble("ok") < 1 ||
                        commandResult.containsKey("writeErrors")) {

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

        int totalInstructions = commandResults.stream().mapToInt(result -> result.getInteger("n")).sum();
        int totalModified = commandResults.stream()
                .mapToInt(result -> Optional.ofNullable(result.getInteger("nModified")).orElse(0))
                .sum();

        log.debug("Number of documents selected" + totalInstructions);
        log.debug("Number of documents selected" + totalModified);
    }

}
