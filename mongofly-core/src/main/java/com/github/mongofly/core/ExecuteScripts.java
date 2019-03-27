package com.github.mongofly.core;

import com.github.mongofly.core.domains.Mongofly;
import com.github.mongofly.core.usecases.GetScriptFiles;
import com.github.mongofly.core.usecases.MongoflyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ExecuteScripts {

    private GetScriptFiles getScriptFiles;

    private MongoflyRepository mongoflyRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void execute() {

        List<Path> scripts = getScriptFiles.get();

        getScriptFiles.get().stream()
                .filter(this::isUnexecutedScripts)
                .sorted(Comparator.comparing(this::getFileName))
                .forEach(this::fileProcess);

    }

    private boolean isUnexecutedScripts(Path path) {

        String scriptName = getFileName(path);
        String version = scriptName.split("__")[0];

        Optional<Mongofly> execution = mongoflyRepository.findByVersion(version);
        return !execution.isPresent();
    }

    private String getFileName(Path path) {

        return path.getFileName().toString();
    }

    private void fileProcess(Path path) {


    }

}
