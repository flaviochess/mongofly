package com.github.mongofly.core.commands;

import com.github.mongofly.core.commands.insert.RunInsertCommand;
import com.github.mongofly.core.commands.update.RunUpdateCommand;
import com.github.mongofly.core.domains.CommandType;
import com.github.mongofly.core.utils.MongoflyException;
import com.mongodb.client.MongoDatabase;

public class RunCommandFactory {

    private RunInsertCommand runInsertCommand;
    private RunUpdateCommand runUpdateCommand;

    private MongoDatabase db;

    public RunCommandFactory(MongoDatabase db) {

        this.db = db;
    }

    public RunCommand factory(String command) {

        CommandType commandType = GetCommandType.fromCommand(command);

        switch (commandType) {

            case INSERT:
            case INSERT_ONE:
            case INSERT_MANY:
                if (runInsertCommand == null) {
                    runInsertCommand = new RunInsertCommand(db);
                }
                return runInsertCommand;
            case UPDATE:
            case UPDATE_ONE:
            case UPDATE_MANY:
                if (runUpdateCommand == null) {
                    runUpdateCommand = new RunUpdateCommand(db);
                }
                return runUpdateCommand;
            case REMOVE:
            default:
                throw new MongoflyException("Not implemented yet");
        }

    }

}
