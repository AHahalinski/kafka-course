package com.epam.learning.kafka.api;

import com.epam.learning.kafka.entity.VehicleLocation;
import com.epam.learning.kafka.service.TaxiService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/taxi")
public class TaxiController {

    private final TaxiService taxiService;

    public TaxiController(TaxiService taxiService) {
        this.taxiService = taxiService;
    }

    @PostMapping("/location")
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleLocation submitLocation(@RequestBody VehicleLocation location) {
        taxiService.submitTaxiLocation(location);
        return location;
    }

    @GetMapping("/ping")
    @ResponseStatus(HttpStatus.OK)
    public String ping() {
        return "Ok";
    }
}
