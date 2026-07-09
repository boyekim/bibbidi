package com.bibbidi.support.exception;

import com.bibbidi.support.exception.errors.Errors;
import lombok.Getter;

@Getter
public abstract class BibbidiException extends RuntimeException {

    private final Errors errors;

    protected BibbidiException(Errors errors, Object... args) {
        super(String.format(errors.getMessage(), args));
        this.errors = errors;
    }
}
