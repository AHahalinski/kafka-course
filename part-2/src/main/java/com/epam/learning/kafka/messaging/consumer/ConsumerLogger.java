package com.epam.learning.kafka.messaging.consumer;

import com.epam.learning.kafka.entity.LatestDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class ConsumerLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerLogger.class.getName());

    @KafkaListener(topics = "${spring.kafka.topic.output}")
    public void logDistance(@Payload LatestDistance latestDistance) {

        LOGGER.info("\nTaxi id: {}" +
                "\ndistance: {}", latestDistance.getVehicleId(), latestDistance.getDistance());
    }
}
