package com.epam.learning.kafka.validator;

import com.epam.learning.kafka.entity.Location;
import com.epam.learning.kafka.exception.ValidationException;
import com.epam.learning.kafka.exception.VehicleValidationException;
import org.springframework.stereotype.Component;

@Component("locationValidator")
public class LocationValidator implements Validator<Location> {

    private static final int LONGITUDE_MAX_VALUE = 180;
    private static final int LONGITUDE_MIN_VALUE = -LONGITUDE_MAX_VALUE;
    private static final int LATITUDE_MAX_VALUE = 90;
    private static final int LATITUDE_MIN_VALUE = -LATITUDE_MAX_VALUE;

    @Override
    public void validate(Location location) throws ValidationException {
        if (location == null) {
            throw new VehicleValidationException("Location cannot be blank");
        }
        validateLatitude(location.getLatitude());
        validateLongitude(location.getLongitude());
    }

    private void validateLongitude(double longitude) {
        if (LONGITUDE_MIN_VALUE > longitude || longitude > LONGITUDE_MAX_VALUE) {
            throw new VehicleValidationException("Longitude is not correct");
        }
    }

    private void validateLatitude(double latitude) {
        if (LATITUDE_MIN_VALUE > latitude || latitude > LATITUDE_MAX_VALUE) {
            throw new VehicleValidationException("Latitude is not correct");
        }
    }
}
