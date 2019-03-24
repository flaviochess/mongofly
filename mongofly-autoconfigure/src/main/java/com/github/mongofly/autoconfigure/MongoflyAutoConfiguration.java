package com.github.mongofly.autoconfigure;

import com.github.mongofly.core.PrintDBData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class MongoflyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PrintDBData printDBData() {

        System.out.println("entrou");
        return new PrintDBData();
    }

}
