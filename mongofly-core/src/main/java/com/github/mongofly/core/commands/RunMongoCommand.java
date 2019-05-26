package com.github.mongofly.core.commands;

import com.github.mongofly.core.exceptions.MongoflyException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunMongoCommand {

    private static final String COMMAND_PREFIX = "db.";

    private RunCommandFactory runCommandFactory;

    public RunMongoCommand(RunCommandFactory runCommandFactory) {
        this.runCommandFactory = runCommandFactory;
    }

    public void run(String command) {

        log.debug("Trying to execute: " + command);

        if (!command.startsWith(COMMAND_PREFIX)) {
            throw new MongoflyException("It's not a valid command. This does not start with \"db...\": " + command);
        }

        try {

            runCommandFactory.factory(command).run(command);

        } catch (RuntimeException ex) {

            log.error("Error trying to execute the command: " + command);
            throw ex;
        }

    }

}
