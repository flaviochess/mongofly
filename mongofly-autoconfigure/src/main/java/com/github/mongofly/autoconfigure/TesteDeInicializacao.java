package com.github.mongofly.autoconfigure;

import com.github.mongofly.core.PrintDBData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author Flavio Andrade <onsave.com.br>
 */
@Component
public class TesteDeInicializacao {

    @Autowired
    PrintDBData printDBData;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {

        printDBData.print();
    }

}
