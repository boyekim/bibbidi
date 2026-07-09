package com.bibbidi.support.exception;

import com.bibbidi.support.exception.errors.Errors;

public class UnauthorizedException extends BibbidiException {

    public UnauthorizedException(Errors errors, Object... args) {
        super(errors, args);
    }
}
