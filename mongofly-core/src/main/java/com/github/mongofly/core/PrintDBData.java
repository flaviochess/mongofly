package com.github.mongofly.core;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class PrintDBData {

    @EventListener(ApplicationReadyEvent.class)
    public void print() {

        System.out.println("Hello Mongofly");

        Path folder = Paths.get("mongofly");

        if(isValidDirectory(folder)) {
            System.out.println("diretorio encontrado");
        }

        DirectoryStream<Path> configurationPaths;

        try {

            configurationPaths = Files.newDirectoryStream(folder, p -> p.getFileName().toString().endsWith(".json"));

        } catch (IOException iOException) {

            System.out.println("Fail to find files in the folder .../mongofly");
            throw new RuntimeException();
        }

        for (Path configurationPath : configurationPaths) {

            System.out.println("Tries to read the file " + configurationPath.getFileName());
        }
    }

    private boolean isValidDirectory(Path path) {

        return Files.exists(path) && Files.isDirectory(path);
    }

}