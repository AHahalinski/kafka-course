package com.epam.learning.kafka.entity;


public class VehicleLocation {

    private String id;

    private Location location;

    public VehicleLocation() {
    }

    public VehicleLocation(String id, Location location) {
        this.id = id;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "VehicleLocation{" +
                "id='" + id + '\'' +
                ", location=" + location +
                '}';
    }
}
