package com.github.mongofly.core.commands;

import com.github.mongofly.core.converts.CommandConvertFactory;
import com.github.mongofly.core.utils.MongoflyException;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
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

        List<DBObject> convertedCommands = commandConvertFactory.factory(command).convert(command);

        List<CommandResult> commandResults = new ArrayList();

        convertedCommands.forEach(convertedCommand -> {
            CommandResult commandResult = mongoTemplate.executeCommand(convertedCommand);
            if (!commandResult.ok()) {

                log.debug("An error occurred while attempting to execute the command: " + convertedCommand);
                throw new MongoflyException("An error occurred while executing" + commandResult.getErrorMessage());
            }

            commandResults.add(commandResult);
        });

        int totalInserts = commandResults.stream().mapToInt(result -> result.getInt("n")).sum();

        log.debug("Number of documents selected" + totalInserts);
    }

}
