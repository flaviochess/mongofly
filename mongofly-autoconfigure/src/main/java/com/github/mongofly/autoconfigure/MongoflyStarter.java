package com.github.mongofly.autoconfigure;

import com.github.mongofly.core.ExecuteScripts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
public class MongoflyStarter {

    private final ExecuteScripts executeScripts;

    public MongoflyStarter(ExecuteScripts executeScripts) {
        this.executeScripts = executeScripts;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void execute() {

        log.info("Mongofly Spring Boot Starter");

        executeScripts.execute();
    }

}
