package com.bibbidi.support.exception;

import com.bibbidi.support.exception.errors.Errors;

public class NotFoundException extends BibbidiException {

    public NotFoundException(Errors errors, Object... args) {
        super(errors, args);
    }
}
