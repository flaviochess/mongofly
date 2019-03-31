package com.github.mongofly.core.usecases;

import org.springframework.stereotype.Component;

@Component
public class RunMongoCommand {

    public void run(String command) {

        System.out.println(command);

        //db.collection.insert({param1: value1, param2: value2, ...});

        //verifica se começa com db.collection
        //extrai o tipo de comando (insert, update, remove, ...) e passa para o método responsável
           //a principio apenas haverá um insert e simples

        //da para depois fazer uma factory para chamar uma classe especilista de acordo com o tipo de comando
    }

    private void insert(String command) {

    }

}
