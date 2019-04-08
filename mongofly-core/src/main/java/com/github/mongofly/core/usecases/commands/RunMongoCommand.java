package com.github.mongofly.core.usecases.commands;

import com.github.mongofly.core.usecases.converts.CommandConvertFactory;
import com.github.mongofly.core.utils.GetCollectionNameFromCommand;
import com.mongodb.CommandResult;
import com.mongodb.DBObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RunMongoCommand {

    private static final String COMMAND_PREFIX = "db.";
    private static final String PARENTHESES_OPEN = "(";
    private static final String PARENTHESES_CLOSE = ")";

    private CommandConvertFactory commandConvertFactory;

    private MongoTemplate mongoTemplate;

    @Autowired
    public RunMongoCommand(CommandConvertFactory commandConvertFactory, MongoTemplate mongoTemplate) {
        this.commandConvertFactory = commandConvertFactory;
        this.mongoTemplate = mongoTemplate;
    }

    public void run(String command) {

//        System.out.println("grande teste");
//        String jsonBody = "{\"version\" : 10, \"script\" : \"v10__test_run_command.json\", \"executed_on\" : { \"$date\" : \"2019-04-07T17:56:38.041Z\" }, \"sucess\" : true}";
//        BasicDBObject dbObject = new BasicDBObject();
//        dbObject.append("insert", "mongofly");
//        try {
//            System.out.println("++++++ " + new ObjectMapper().readTree(jsonBody));
//            dbObject.append("documents", Arrays.asList(Document.parse(jsonBody)));
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("falhou o grande teste");
//        }
//        System.out.println("****** " + dbObject.toJson());
//        //String grandeTest = "{insert: \"mongofly\", documents: [{\"version\" : 10, \"script\" : \"v10__test_run_command.json\", \"executed_on\" : { \"$date\" : \"2019-04-07T17:56:38.041Z\" }, \"sucess\" : true}]}";
//        mongoTemplate.executeCommand(dbObject.toJson());
//        if(!dbObject.isEmpty()) {
//            throw new RuntimeException("rodou o grande teste");
//        }

        log.debug(command);

        if (!command.startsWith(COMMAND_PREFIX)) {
            throw new RuntimeException();
        }

        List<DBObject> convertedCommands = commandConvertFactory.factory(command).convert(command);

        List<CommandResult> commandResults = new ArrayList();

        convertedCommands.forEach(convertedCommand -> {
            CommandResult commandResult = mongoTemplate.executeCommand(convertedCommand);
            if (!commandResult.ok()) {
                throw new RuntimeException(commandResult.getErrorMessage());
            }

            commandResults.add(commandResult);
        });

        int totalInserts = commandResults.stream().mapToInt(result -> result.getInt("n")).sum();

        //return ou debug totalInserts
    }

}
