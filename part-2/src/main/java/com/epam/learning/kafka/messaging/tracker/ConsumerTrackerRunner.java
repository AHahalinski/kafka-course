package com.epam.learning.kafka.messaging.tracker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerTrackerRunner {

    private final int threadCount;

    private final ExecutorService executor;

    private final TrackerFactory trackerFactory;

    public ConsumerTrackerRunner(int threadCount, TrackerFactory trackerFactory) {

        this.threadCount = threadCount;
        this.trackerFactory = trackerFactory;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    public void init() {
        for (int i = 0; i < threadCount; i++) {
            executor.submit(trackerFactory.create(i));
        }
    }
}
