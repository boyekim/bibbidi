package com.bibbidi.support.exception;

import com.bibbidi.support.exception.errors.Errors;

public class InternalServerException extends BibbidiException {

    public InternalServerException(Errors errors, Object... args) {
        super(errors, args);
    }
}
