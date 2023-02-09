package com.epam.learning.kafka.exception;

public abstract class ValidationException extends RuntimeException {

    protected ValidationException(String message) {
        super(message);
    }

    protected ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
