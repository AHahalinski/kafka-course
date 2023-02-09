package com.epam.learning.kafka.validator;

import com.epam.learning.kafka.exception.ValidationException;

public interface Validator<O> {

    void validate(O obj) throws ValidationException;
}
