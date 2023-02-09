package com.epam.learning.kafka.messaging.entity;

import com.epam.learning.kafka.entity.LatestDistance;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.List;
import java.util.Map;

public class SendStageInput {

    private final List<LatestDistance> latestDistances;

    private final Map<TopicPartition, OffsetAndMetadata> offsets;

    public SendStageInput(List<LatestDistance> latestDistances, Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.latestDistances = latestDistances;
        this.offsets = offsets;
    }

    public boolean isEmpty() {
        return latestDistances.isEmpty();
    }

    public List<LatestDistance> getLatestDistances() {
        return latestDistances;
    }

    public Map<TopicPartition, OffsetAndMetadata> getOffsets() {
        return offsets;
    }

}
