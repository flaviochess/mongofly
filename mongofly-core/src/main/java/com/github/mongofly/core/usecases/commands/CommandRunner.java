package com.github.mongofly.core.usecases.commands;

public interface CommandRunner {

    void run(String collectionName, String commandBody);
}
