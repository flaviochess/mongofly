package com.github.mongofly.core.commands;

import com.github.mongofly.core.commands.createindex.RunCreateIndexCommand;
import com.github.mongofly.core.commands.insert.RunInsertCommand;
import com.github.mongofly.core.commands.remove.RunDeleteCommand;
import com.github.mongofly.core.commands.update.RunUpdateCommand;
import com.github.mongofly.core.commands.utils.GetCommandType;
import com.github.mongofly.core.domains.CommandType;
import com.github.mongofly.core.exceptions.MongoflyException;
import com.mongodb.client.MongoDatabase;

public class RunCommandFactory {

    private RunInsertCommand runInsertCommand;
    private RunUpdateCommand runUpdateCommand;
    private RunDeleteCommand runDeleteCommand;
    private RunCreateIndexCommand runCreateIndexCommand;

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
            case DELETE_ONE:
            case DELETE_MANY:
                if (runDeleteCommand == null) {
                    runDeleteCommand = new RunDeleteCommand(db);
                }
                return runDeleteCommand;
            case CREATE_INDEX:
                if (runCreateIndexCommand == null) {
                    runCreateIndexCommand = new RunCreateIndexCommand(db);
                }
                return runCreateIndexCommand;
            default:
                throw new MongoflyException("Not implemented yet");
        }

    }

}
