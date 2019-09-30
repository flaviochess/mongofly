package com.github.mongofly.core;

import com.github.mongofly.core.commands.RunCommandFactory;
import com.github.mongofly.core.commands.RunMongoCommand;
import com.github.mongofly.core.commands.strictmode.ConvertToStrictMode;
import com.github.mongofly.core.domains.Mongofly;
import com.github.mongofly.core.domains.MongoflyRepository;
import com.github.mongofly.core.exceptions.MongoflyException;
import com.github.mongofly.core.scripts.GetScriptFiles;
import com.github.mongofly.core.scripts.GetScriptsFromClasspath;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class ExecuteScripts {

    private GetScriptFiles getScriptFiles;

    private GetScriptsFromClasspath getScriptsFromClasspath;

    private MongoflyRepository mongoflyRepository;

    private MongoDatabase mongoDatabase;

    private RunCommandFactory runCommandFactory;

    private RunMongoCommand runMongoCommand;

    public ExecuteScripts(MongoDatabase mongoDatabase) {

        this.mongoDatabase = mongoDatabase;

        this.mongoflyRepository = new MongoflyRepository(mongoDatabase);
        this.getScriptFiles = new GetScriptFiles();
        this.getScriptsFromClasspath = new GetScriptsFromClasspath();
        this.runCommandFactory = new RunCommandFactory(this.mongoDatabase);
        this.runMongoCommand = new RunMongoCommand(runCommandFactory, new ConvertToStrictMode());
    }

    public void execute() {

        log.info("Starting Mongofly");

        Stream.concat(
            getScriptFiles.get().stream(),
            getScriptsFromClasspath.get().stream())
                .filter(this::isUnexecutedScripts)
                .sorted(Comparator.comparing(this::getFileName))
                .forEach(this::fileProcess);

        log.info("Finish Mongofly");
    }

    private boolean isUnexecutedScripts(Path path) {

        String version = getFileVersion(path);
        Optional<Mongofly> execution = mongoflyRepository.findByVersion(version);

        log.debug("Script " + version + " executed: " + execution.isPresent());

        return !execution.isPresent() || !execution.get().isSuccess();
    }

    private void fileProcess(Path path) {

        String version = getFileVersion(path);
        Mongofly mongofly = mongoflyRepository.findByVersion(version).orElse(newMongofly(path));

        List<String> commands = extractCommands(path);

        try {

            for (String command : commands) {

                runMongoCommand.run(command);
            }

            mongofly.setExecutedOn(new Date());
            mongofly.setSuccess(true);

        } catch (RuntimeException exception) {

            mongofly.setExecutedOn(new Date());
            mongofly.setSuccess(false);
            throw new MongoflyException(exception);

        } finally {

            mongoflyRepository.save(mongofly);
        }

    }

    private Mongofly newMongofly(Path path) {

        Mongofly mongofly = new Mongofly();
        mongofly.setScript(getFileName(path));
        mongofly.setVersion(getFileVersion(path));

        return mongofly;
    }

    private String getFileName(Path path) {

        return path.getFileName().toString();
    }

    private String getFileVersion(Path path) {

        String scriptName = getFileName(path);
        return scriptName.split("__")[0].toUpperCase().replace("V", "");
    }

    private List<String> extractCommands(Path path) {

        List<String> commands = new ArrayList();

        StringBuilder command = new StringBuilder();
        File scriptFile = path.toFile();

        try (BufferedReader br = new BufferedReader(new FileReader(scriptFile))) {

            String line;
            while ((line = br.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                command.append(line);

                if (line.endsWith(";")) {
                    commands.add(command.toString());
                    command = new StringBuilder();
                    continue;
                }

            }

            if (command.length() > 0) {

                throw new MongoflyException("Command don't finish with \";\" - " + command.toString());
            }

        } catch (IOException ioe) {

            throw new RuntimeException();
        }

        return commands;
    }

}
