package com.epam.learning.kafka.messaging.producer;

import com.epam.learning.kafka.entity.VehicleLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private final KafkaTemplate<String, VehicleLocation> kafkaTemplate;

    @Value("${spring.kafka.topic.input}")
    private String topic;

    @Autowired
    public Producer(KafkaTemplate<String, VehicleLocation> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(VehicleLocation vehicleLocation) {
        kafkaTemplate.send(topic, vehicleLocation.getId(), vehicleLocation);
    }
}
