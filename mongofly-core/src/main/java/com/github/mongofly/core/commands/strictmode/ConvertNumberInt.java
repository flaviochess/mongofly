package com.github.mongofly.core.commands.strictmode;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertNumberInt implements StrictModeConverter {

    private static final String REGEX = "NumberInt\\(\\d{0,}\\)";

    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public String toStrictMode(String command) {

        Matcher matcher = PATTERN.matcher(command);

        while (matcher.find()) {

            String value = extractValue(matcher.group());

            StringBuilder strictMode =
                    new StringBuilder("{\"$numberInt\": \"").append(value).append("\"}");

            command = command.replace(matcher.group(), strictMode.toString());
        }

        return command;
    }

    private String extractValue(String numberIntFunction) {

        return StringUtils.substringBetween(numberIntFunction, "NumberInt(", ")");
    }

}
