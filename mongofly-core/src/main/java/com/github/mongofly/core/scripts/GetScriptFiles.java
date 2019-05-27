package com.github.mongofly.core.scripts;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GetScriptFiles {

    private final static String SCRIPTS_CONFIGURATION_PATH = "mongofly";

    public List<Path> get() {

        Path folder = Paths.get(SCRIPTS_CONFIGURATION_PATH);

        if(!isValidDirectory(folder)) {

            log.error("Invalid path configuration files: {}", SCRIPTS_CONFIGURATION_PATH);
            return new ArrayList();
        }

        DirectoryStream<Path> configurationPaths;

        try {

            configurationPaths = Files.newDirectoryStream(folder, p -> p.getFileName().toString().endsWith(".json"));

        } catch (IOException iOException) {

            log.error("Fail to find files in the folder {}", SCRIPTS_CONFIGURATION_PATH, iOException);
            return new ArrayList();
        }

        List<Path> scripts = new ArrayList();
        configurationPaths.forEach(scripts::add);

        log.debug("{} script files found", scripts.size());

        return scripts;
    }

    private boolean isValidDirectory(Path path) {

        return Files.exists(path) && Files.isDirectory(path);
    }
}
