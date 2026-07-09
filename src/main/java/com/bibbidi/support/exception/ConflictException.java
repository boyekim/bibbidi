package com.bibbidi.support.exception;

import com.bibbidi.support.exception.errors.Errors;

public class ConflictException extends BibbidiException {

    public ConflictException(Errors errors, Object... args) {
        super(errors, args);
    }
}
