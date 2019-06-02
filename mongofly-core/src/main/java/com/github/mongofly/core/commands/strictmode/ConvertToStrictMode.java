package com.github.mongofly.core.commands.strictmode;

public class ConvertToStrictMode {

    public String convert(String command) {

        for (StrictMode strictMode : StrictMode.values()) {
            command = strictMode.convert(command);
        }

        return command;
    }
}
