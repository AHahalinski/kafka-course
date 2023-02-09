package com.epam.learning.kafka.messaging.exception;

public class KafkaOperationException extends RuntimeException {

    public KafkaOperationException(String message) {
        super(message);
    }

    public KafkaOperationException(String message, Throwable cause) {
        super(message, cause);
    }

}
