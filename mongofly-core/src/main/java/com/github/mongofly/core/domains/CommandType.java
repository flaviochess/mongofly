package com.github.mongofly.core.domains;

public enum CommandType {

    INSERT("insert"),
    INSERT_ONE("insertOne"),
    INSERT_MANY("insertMany"),
    UPDATE("update"),
    UPDATE_ONE("updateOne"),
    UPDATE_MANY("updateMany"),
    REMOVE("delete");

    private String value;

    CommandType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
