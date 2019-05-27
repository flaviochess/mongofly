package com.github.mongofly.autoconfigure;

import com.github.mongofly.core.ExecuteScripts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoflyAutoConfiguration {

    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    @ConditionalOnMissingBean
    public ExecuteScripts executeScripts() {

        return new ExecuteScripts(mongoTemplate.getDb());
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoflyStarter mongoflyStarter() {

        return new MongoflyStarter(executeScripts());
    }
}
