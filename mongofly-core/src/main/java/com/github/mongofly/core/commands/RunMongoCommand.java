package com.github.mongofly.core.commands;

import com.github.mongofly.core.commands.strictmode.ConvertToStrictMode;
import com.github.mongofly.core.exceptions.MongoflyException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunMongoCommand {

    private static final String COMMAND_PREFIX = "db.";

    private final RunCommandFactory runCommandFactory;

    private final ConvertToStrictMode convertToStrictMode;

    public RunMongoCommand(RunCommandFactory runCommandFactory, ConvertToStrictMode convertToStrictMode) {
        this.runCommandFactory = runCommandFactory;
        this.convertToStrictMode = convertToStrictMode;
    }

    public void run(String command) {

        log.debug("Trying to execute: " + command);

        if (!command.startsWith(COMMAND_PREFIX)) {
            throw new MongoflyException("It's not a valid command. This does not start with \"db...\": " + command);
        }

        String convertedCommand = convertToStrictMode.convert(command);

        try {

            runCommandFactory.factory(convertedCommand).run(convertedCommand);

        } catch (RuntimeException ex) {

            log.error("Error trying to execute the command: " + command);
            throw ex;
        }

    }

}
