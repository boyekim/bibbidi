package com.bibbidi.support.exception;

import com.bibbidi.support.exception.errors.Errors;

public class BadRequestException extends BibbidiException {

    public BadRequestException(Errors errors, Object... args) {
        super(errors, args);
    }
}
