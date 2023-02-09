package com.epam.learning.kafka.entity;

public class LatestDistance {

    private String vehicleId;

    private double distance;

    public LatestDistance() {
    }

    public LatestDistance(String vehicleId, double distance) {
        this.vehicleId = vehicleId;
        this.distance = distance;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "LatestDistance{" +
                "vehicleId='" + vehicleId + '\'' +
                ", distance=" + distance +
                '}';
    }
}
