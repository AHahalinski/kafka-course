package com.epam.learning.kafka.validator;

import com.epam.learning.kafka.entity.Location;
import com.epam.learning.kafka.entity.VehicleLocation;
import com.epam.learning.kafka.exception.ValidationException;
import com.epam.learning.kafka.exception.VehicleValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("vehicleValidator")
public class VehicleValidator implements Validator<VehicleLocation> {

    public static final String ERROR_MESSAGE_VEHICLE_ID = "id: '%s' is not valid";

    private final Validator<Location> locationValidator;

    public VehicleValidator(@Qualifier("locationValidator") Validator<Location> locationValidator) {
        this.locationValidator = locationValidator;
    }

    public void validate(VehicleLocation vehicleLocation) throws ValidationException {
        validateVehicleId(vehicleLocation.getId());
        locationValidator.validate(vehicleLocation.getLocation());

    }

    private void validateVehicleId(String id) {
        if (id.isBlank()) {
            throw new VehicleValidationException(String.format(ERROR_MESSAGE_VEHICLE_ID, id));
        }
    }
}
