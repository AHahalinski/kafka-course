package com.epam.learning.kafka;

import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

public class TestDataGenerator {

    private final static int MIN_LENGTH = 5;

    public static List<String> generateMessages(int count, int maxLengthOfMessage) {
        List<String> messages = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            messages.add(RandomStringUtils.randomAlphabetic(MIN_LENGTH, maxLengthOfMessage));
        }
        return messages;
    }
}
