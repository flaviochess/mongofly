package com.github.mongofly.core.usecases.converts;

import com.mongodb.DBObject;

import java.util.List;

public interface CommandConvert {

    public static final int DOCUMENTS_LIMIT_SIZE = 1000;

    //TODO: retornar uma lista, pois para comandos muito grandes, o retorno será quebrado
    public List<DBObject> convert(String command);
}
