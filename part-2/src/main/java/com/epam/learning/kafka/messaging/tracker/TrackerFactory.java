package com.epam.learning.kafka.messaging.tracker;

import com.epam.learning.kafka.entity.LatestDistance;
import com.epam.learning.kafka.entity.VehicleLocation;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TrackerFactory {

    private final String inputTopic;

    private final String outputTopic;

    private final Properties consumerProperties;

    private final Properties producerProperties;

    private final Duration pollingDuration;

    private final ConcurrentMap<String, VehicleLocation> cache = new ConcurrentHashMap<>();

    public TrackerFactory(String inputTopic,
                          String outputTopic,
                          Properties consumerProperties,
                          Properties producerProperties,
                          Duration pollingDuration) {
        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
        this.consumerProperties = consumerProperties;
        this.producerProperties = producerProperties;
        this.pollingDuration = pollingDuration;
    }

    public Tracker create(int number) {

        KafkaConsumer<String, VehicleLocation> consumer = createConsumer();

        KafkaProducer<String, LatestDistance> producer = createProducer(number);

        return new Tracker(outputTopic, consumer, producer, pollingDuration, cache);
    }

    private KafkaProducer<String, LatestDistance> createProducer(int number) {
        String transactionalId = producerProperties.get(ProducerConfig.TRANSACTIONAL_ID_CONFIG) + "_" + number;
        producerProperties.replace(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        KafkaProducer<String, LatestDistance> producer =
                new KafkaProducer<>(producerProperties,
                        new StringSerializer(),
                        new JsonSerializer<>());
        producer.initTransactions();
        return producer;
    }

    private KafkaConsumer<String, VehicleLocation> createConsumer() {
        KafkaConsumer<String, VehicleLocation> consumer =
                new KafkaConsumer<>(consumerProperties,
                        new StringDeserializer(),
                        new JsonDeserializer<>(VehicleLocation.class));
        consumer.subscribe(Collections.singleton(inputTopic));
        return consumer;
    }
}
