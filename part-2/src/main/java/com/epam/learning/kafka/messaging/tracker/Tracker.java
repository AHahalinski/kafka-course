package com.epam.learning.kafka.messaging.tracker;

import com.epam.learning.kafka.entity.LatestDistance;
import com.epam.learning.kafka.messaging.entity.SendStageInput;
import com.epam.learning.kafka.entity.VehicleLocation;
import com.epam.learning.kafka.messaging.exception.KafkaOperationException;
import com.epam.learning.kafka.messaging.utils.DistanceCalculator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Tracker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tracker.class);

    private final String resultTopicName;

    private final KafkaProducer<String, LatestDistance> producer;

    private final KafkaConsumer<String, VehicleLocation> consumer;

    private final Duration pollingDuration;

    private final ConcurrentMap<String, VehicleLocation> cache;

    public Tracker(String outputTopic,
                   KafkaConsumer<String, VehicleLocation> consumer,
                   KafkaProducer<String, LatestDistance> producer,
                   Duration pollingDuration,
                   ConcurrentMap<String, VehicleLocation> cache) {

        this.resultTopicName = notBlank(outputTopic, "result topic name is required");
        this.producer = notNull(producer, "results producer is required");
        this.consumer = notNull(consumer, "vehicle location consumer is required");
        this.pollingDuration = notNull(pollingDuration, "polling duration is required");
        this.cache = notNull(cache, "cache is required");
    }

    public void processLocationBatch() {
        SendStageInput distancesData = processInputData();

        inTransaction(() -> distancesData.getLatestDistances().forEach(this::send), distancesData.getOffsets());
    }

    private SendStageInput processInputData() {
        ConsumerRecords<String, VehicleLocation> records = consumer.poll(pollingDuration);

        List<LatestDistance> distances =
                StreamSupport.stream(records.spliterator(), false)
                             .map(this::convertToLatestDistance)
                             .collect(Collectors.toList());

        Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
        records.partitions().forEach(partition -> {
            var partitionedRecords = records.records(partition);
            var offset = partitionedRecords.get(partitionedRecords.size() - 1).offset() + 1;
            offsets.put(partition, new OffsetAndMetadata(offset));
        });

        return new SendStageInput(distances, offsets);
    }

    private LatestDistance convertToLatestDistance(ConsumerRecord<String, VehicleLocation> consumerRecord) {
        VehicleLocation current = consumerRecord.value();
        var previous = cache.getOrDefault(current.getId(), current);

        cache.put(current.getId(), current);
        return DistanceCalculator.calculateDistance(current, previous);
    }

    private void inTransaction(Runnable logic, Map<TopicPartition, OffsetAndMetadata> offsets) {
        producer.beginTransaction();

        try {
            logic.run();
        } catch (KafkaOperationException e) {
            producer.abortTransaction();
            return;
        }

        producer.sendOffsetsToTransaction(offsets, consumer.groupMetadata());
        producer.commitTransaction();
    }

    private void send(LatestDistance distance) {
        ProducerRecord<String, LatestDistance> producerRecord =
                new ProducerRecord<>(resultTopicName, distance.getVehicleId(), distance);
        try {
            producer.send(producerRecord).get();
        } catch (Exception e) {
            LOGGER.error("failed to send a record", e);
            throw new KafkaOperationException("failed to send a record", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            processLocationBatch();
        }
    }
}
