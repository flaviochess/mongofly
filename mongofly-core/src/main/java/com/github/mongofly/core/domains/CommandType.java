package com.github.mongofly.core.domains;

import com.github.mongofly.core.utils.MongoflyException;

import java.util.Arrays;
import java.util.Optional;

public enum CommandType {

    INSERT("insert"),
    INSERT_ONE("insertOne"),
    INSERT_MANY("insertMany"),
    UPDATE("update"),
    UPDATE_ONE("updateOne"),
    UPDATE_MANY("updateMany"),
    REMOVE("remove"),
    DELETE_ONE("deleteOne"),
    DELETE_MANY("deleteMany");

    private String value;

    CommandType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static CommandType fromValue(String value) {

        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new MongoflyException("Command name wrong or not implemented yet: " + value));

    }

}
