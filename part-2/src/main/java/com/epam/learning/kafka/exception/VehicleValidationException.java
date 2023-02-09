package com.epam.learning.kafka.exception;

public class VehicleValidationException extends ValidationException {

    public VehicleValidationException(String message) {
        super(message);
    }

    public VehicleValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
