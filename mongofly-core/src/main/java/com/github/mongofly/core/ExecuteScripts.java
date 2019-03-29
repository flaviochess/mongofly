package com.github.mongofly.core;

import com.github.mongofly.core.domains.Mongofly;
import com.github.mongofly.core.usecases.GetScriptFiles;
import com.github.mongofly.core.usecases.MongoflyRepository;
import com.github.mongofly.core.usecases.RunMongoCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;

@Slf4j
@Component
public class ExecuteScripts {

    private GetScriptFiles getScriptFiles;

    private MongoflyRepository mongoflyRepository;

    private RunMongoCommand runMongoCommand;

    @EventListener(ApplicationReadyEvent.class)
    public void execute() {

        List<Path> scripts = getScriptFiles.get();

        /* confirmar se a ordenação está correta */
        getScriptFiles.get().stream()
                .filter(this::isUnexecutedScripts)
                .sorted(Comparator.comparing(this::getFileName))
                .forEach(this::fileProcess);

    }

    private boolean isUnexecutedScripts(Path path) {

        String version = getFileVersion(path);
        Optional<Mongofly> execution = mongoflyRepository.findByVersion(version);
        return !execution.isPresent();
    }

    private String getFileName(Path path) {

        return path.getFileName().toString();
    }

    private void fileProcess(Path path) {

        Mongofly mongofly = new Mongofly();
        mongofly.setScript(getFileName(path));
        mongofly.setVersion(getFileVersion(path));

        String command = "";
        //extrair cada linha executável

        //chamar o executador de comandos

        try {

            runMongoCommand.run(command);
            mongofly.setExecutedOn(new Date());
            mongofly.setSuccess(true);

        } catch (RuntimeException exception) {

            mongofly.setExecutedOn(new Date());
            mongofly.setSuccess(false);
            throw exception;

        } finally {

            mongoflyRepository.insert(mongofly);
        }

    }

    private String getFileVersion(Path path) {

        String scriptName = getFileName(path);
        return scriptName.split("__")[0];
    }

}
