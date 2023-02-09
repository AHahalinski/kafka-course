package com.epam.learning.kafka.messaging.configuration;

import com.epam.learning.kafka.entity.LatestDistance;
import com.epam.learning.kafka.entity.VehicleLocation;
import com.epam.learning.kafka.messaging.tracker.ConsumerTrackerRunner;
import com.epam.learning.kafka.messaging.tracker.TrackerFactory;
import org.apache.commons.lang3.Validate;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Configuration
@EnableKafka
public class KafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value("${spring.kafka.topic.input}")
    private String inputTopic;

    @Value("${spring.kafka.topic.output}")
    private String outputTopic;

    @Value("${spring.kafka.replication.factor:1}")
    private short replicationFactor;

    @Value("${spring.kafka.partition.number:1}")
    private int partitionNumber;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ProducerFactory<String, VehicleLocation> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, VehicleLocation> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic createInputLocationTopic() {
        Validate.notNull(outputTopic, "Topic name is required");
        return new NewTopic(inputTopic, partitionNumber, replicationFactor);
    }

    @Bean
    public NewTopic createOutputDistanceTopic() {
        Validate.notNull(outputTopic, "Topic name is required");
        return new NewTopic(outputTopic, partitionNumber, replicationFactor);
    }

    @Bean
    public ConsumerFactory<String, LatestDistance> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "_logger");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return new DefaultKafkaConsumerFactory<>(props,
                                                new StringDeserializer(),
                                                new JsonDeserializer<>(LatestDistance.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, LatestDistance> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, LatestDistance> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

    private Properties consumerTrackerProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    private Properties producerTrackerProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "tx_id_tracker");
        return props;
    }

    @Bean
    public TrackerFactory trackerFactory() {
        return new TrackerFactory(inputTopic,
                                  outputTopic,
                                  consumerTrackerProperties(),
                                  producerTrackerProperties(),
                                  Duration.ofMillis(500));
    }

    @Bean(initMethod = "init")
    public ConsumerTrackerRunner trackerRunner(TrackerFactory trackerFactory) {
        return new ConsumerTrackerRunner(partitionNumber, trackerFactory);
    }
}
