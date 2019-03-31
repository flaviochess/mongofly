package com.github.mongofly.autoconfigure;

import com.github.mongofly.core.ExecuteScripts;
import com.github.mongofly.core.usecases.GetScriptFiles;
import com.github.mongofly.core.usecases.MongoflyRepository;
import com.github.mongofly.core.usecases.RunMongoCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

@Configuration
public class MongoflyAutoConfiguration {

    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    @ConditionalOnMissingBean
    public ExecuteScripts printDBData() {

        System.out.println("entrou");
        return new ExecuteScripts();
    }

    @Bean
    @ConditionalOnMissingBean
    public GetScriptFiles getScriptFiles() {

        return new GetScriptFiles();
    }

    @Bean
    @ConditionalOnMissingBean
    public RunMongoCommand runMongoCommand() {

        return new RunMongoCommand();
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoflyRepository mongoflyRepository() {

        return new MongoRepositoryFactory(mongoTemplate).getRepository(MongoflyRepository.class);
    }
}
