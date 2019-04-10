package com.github.mongofly.core.utils;

public class MongoflyException extends RuntimeException {

    public MongoflyException(String message) {
        super(message);
    }

    public MongoflyException(String message, Throwable cause) {
        super(message, cause);
    }

}