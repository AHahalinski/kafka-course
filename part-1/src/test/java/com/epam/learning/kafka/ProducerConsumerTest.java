package com.epam.learning.kafka;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(classes = {KafkaConfiguration.class, Producer.class, Consumer.class})
class ProducerConsumerTest {

    public static final String KAFKA_IMAGE = "confluentinc/cp-kafka:latest";
    public static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(KAFKA_IMAGE);

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(DOCKER_IMAGE_NAME);

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        // for consumer "at most once"
        registry.add("spring.kafka.consumer.enableAutoCommit", () -> true);
        registry.add("spring.kafka.consumer.autoCommitIntervalMs", () -> 100);
    }

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

    @BeforeAll
    public static void setUp() {
        kafkaContainer.withReuse(true);
        kafkaContainer.start();
    }

    @Test
    void testProducerAtLeastOnesBehavior() throws InterruptedException {
        // given
        int numberOfMessages = 100;
        consumer.setCountDownLatch(numberOfMessages);

        // when
        for (String message : TestDataGenerator.generateMessages(numberOfMessages, 15)) {
            producer.send(message);
        }
        boolean allMessageConsumed = consumer.getDownLatch().await(numberOfMessages / 4, TimeUnit.SECONDS);

        // then
        assertTrue(allMessageConsumed);
    }

    @Test
    void testConsumerAtMostOnesBehavior() throws InterruptedException {
        // given
        final String lastMessage = "Last message";

        final int numberOfMessages = 100;
        consumer.setCountDownLatch(numberOfMessages);

        final List<String> messages = generateMessages(lastMessage, numberOfMessages);
        final long amountSkippedMessages =
                messages.stream()
                        .filter(m -> m.length() >= consumer.getMaxLengthMessage())
                        .count();

        // when
        messages.forEach(producer::send);
        consumer.getDownLatch().await(numberOfMessages / 4, TimeUnit.SECONDS);

        // then
        assertEquals(lastMessage, consumer.getLastReceivedMessage());
        assertEquals(amountSkippedMessages, consumer.getDownLatch().getCount());
    }

    @NotNull
    private static List<String> generateMessages(String lastMessage, int numberOfMessages) {
        int maxLengthOfMessage = 26;
        List<String> messages = TestDataGenerator.generateMessages(numberOfMessages, maxLengthOfMessage);
        messages.set(numberOfMessages - 1, lastMessage);
        return messages;
    }

    @AfterAll
    public static void cleanContainer() {
        kafkaContainer.stop();
    }
}