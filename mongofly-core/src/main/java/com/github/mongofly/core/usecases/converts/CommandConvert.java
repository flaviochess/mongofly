package com.github.mongofly.core.usecases.converts;

import com.mongodb.DBObject;

public interface CommandConvert {

    //TODO: retornar uma lista, pois para comandos muito grandes, o retorno será quebrado
    public DBObject convert(String command);
}
