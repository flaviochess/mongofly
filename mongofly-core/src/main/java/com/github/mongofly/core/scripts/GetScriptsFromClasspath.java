package com.github.mongofly.core.scripts;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class GetScriptsFromClasspath {

    public static final String MONGOFLY_RES_FOLDER = "mongofly";

    public List<Path> get() {

        URL resource = this.getClass().getClassLoader().getResource(MONGOFLY_RES_FOLDER);

        try {
            assert resource != null;
            Path path = Path.of(resource.toURI());
            if (isValidDirectory(path)) {
                ArrayList<Path> paths = Lists.newArrayList();
                Files.newDirectoryStream(path, p -> p.getFileName().toString().endsWith(".json"))
                    .iterator().forEachRemaining(paths::add);

                return paths;
            } else {
                return Lists.newArrayList();
            }
        } catch (URISyntaxException | IOException e) {
            log.error("Failed to find mongofly files in classpath resources", e);
            return Lists.newArrayList();
        }
    }

    private boolean isValidDirectory(Path path) {

        return Files.exists(path) && Files.isDirectory(path);
    }
}
