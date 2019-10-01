package com.github.mongofly.core.scripts;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GetScriptsFromClasspath {

    public static final String MONGOFLY_RES_FILES = "mongofly/*.json";

    public List<Resource> get() {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resolver.getResources(MONGOFLY_RES_FILES);

            return Arrays.asList(resources);
        } catch (IOException e) {
            log.error("Error loading mongofly files", e);

            return Lists.newArrayList();
        }
    }
}
