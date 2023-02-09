package com.epam.learning.kafka.api;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ErrorMessage {

    private final int status;
    private final String code;

    private final String message;

    private final LocalDateTime timestamp;

    public ErrorMessage(Exception exception, HttpStatus status) {
        this.status = status.value();
        this.code = status.name();
        this.message = exception.getMessage();
        timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
