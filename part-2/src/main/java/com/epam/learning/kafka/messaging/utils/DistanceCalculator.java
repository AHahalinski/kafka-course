package com.epam.learning.kafka.messaging.utils;

import com.epam.learning.kafka.entity.LatestDistance;
import com.epam.learning.kafka.entity.Location;
import com.epam.learning.kafka.entity.VehicleLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistanceCalculator {

    private DistanceCalculator() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DistanceCalculator.class);

    public static LatestDistance calculateDistance(VehicleLocation current, VehicleLocation previous) {

        LOGGER.info("TaxiId: {} moved from {} to {}", current.getId(), current.getLocation(), previous.getLocation());

        double distance = calculateDistanceBetweenPoints(current.getLocation(), previous.getLocation());
        return new LatestDistance(current.getId(), distance);
    }

    private static double calculateDistanceBetweenPoints(Location current, Location previous) {
        return Math.hypot(
                Math.abs(previous.getLatitude() - current.getLatitude()),
                Math.abs(previous.getLongitude() - current.getLongitude()));
    }
}
