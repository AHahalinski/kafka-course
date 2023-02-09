package com.epam.learning.kafka.exception;

public class LocationValidationException extends ValidationException {

    public LocationValidationException(String message) {
        super(message);
    }

    public LocationValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
