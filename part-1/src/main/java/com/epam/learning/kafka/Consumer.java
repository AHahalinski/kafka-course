package com.epam.learning.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;

@Component
public class Consumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class.getName());
    private static final int MAX_LENGTH_MESSAGE = 25;

    private CountDownLatch downLatch = new CountDownLatch(1);

    private final Deque<String> messages = new ConcurrentLinkedDeque<>();

    @KafkaListener(topics = "${spring.kafka.topic.name}",
                   groupId = "${spring.kafka.consumer.group-id}")
    public void logMessages(@Payload String message,
                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) Integer partition,
                            @Header(KafkaHeaders.OFFSET) Long offset) {
        if (message.length() < MAX_LENGTH_MESSAGE) {
            messages.add(message);
            LOGGER.debug("\nReceived a message: {}" +
                    "\nfrom topic: {}" +
                    "\npartition: {}" +
                    "\noffset: {}", message, topic, partition, offset);

            downLatch.countDown();
        } else {
            LOGGER.debug("Message: {} was skipped", message);
        }
    }

    public CountDownLatch getDownLatch() {
        return downLatch;
    }

    public void setCountDownLatch(int count) {
        this.downLatch = new CountDownLatch(count);
    }

    public String getLastReceivedMessage() {
        return messages.getLast();
    }

    public int getMaxLengthMessage() {
        return MAX_LENGTH_MESSAGE;
    }
}
