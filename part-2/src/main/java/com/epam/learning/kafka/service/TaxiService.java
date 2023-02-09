package com.epam.learning.kafka.service;

import com.epam.learning.kafka.entity.VehicleLocation;
import com.epam.learning.kafka.messaging.producer.Producer;
import com.epam.learning.kafka.validator.VehicleValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class TaxiService {

    private final Producer producer;

    private final VehicleValidator validator;

    public TaxiService(Producer producer,
                       @Qualifier("vehicleValidator") VehicleValidator validator) {
        this.producer = producer;
        this.validator = validator;
    }

    public void submitTaxiLocation(VehicleLocation vehicleLocation) {
        validator.validate(vehicleLocation);
        producer.send(vehicleLocation);
    }
}
