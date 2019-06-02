package com.github.mongofly.core.commands.strictmode;

import java.util.stream.Stream;

public enum StrictMode {

    NUMBER_INT(new ConvertNumberInt());

    private StrictModeConverter strictModeConverter;

    StrictMode(StrictModeConverter strictModeConverter) {
        this.strictModeConverter = strictModeConverter;
    }

    public String convert(String command) {

        return this.strictModeConverter.toStrictMode(command);
    }

}
