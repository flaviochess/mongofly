package com.github.mongofly.core.domains;

public enum CommandType {

    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    private String value;

    CommandType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
