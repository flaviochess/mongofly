package com.github.mongofly.core.exceptions;

public class MongoflyException extends RuntimeException {

    public MongoflyException(String message) {
        super(message);
    }

    public MongoflyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoflyException(Throwable cause) {
        super(cause);
    }
}
