package com.epam.learning.kafka.api;

import com.epam.learning.kafka.exception.VehicleValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(VehicleValidationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleVehicleValidationException(VehicleValidationException ex) {
        return new ErrorMessage(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorMessage handleNotReadableException(HttpMessageNotReadableException ex) {
        return new ErrorMessage(ex, HttpStatus.BAD_REQUEST);
    }

}
