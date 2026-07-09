package com.bibbidi.support.exception;

import com.bibbidi.support.exception.errors.Errors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ErrorResponse(
    String code,
    String message
) {

    public static ResponseEntity<ErrorResponse> of(HttpStatus httpStatus, BibbidiException exception) {
        return ResponseEntity
            .status(httpStatus)
            .body(new ErrorResponse(exception.getErrors().getCode(), exception.getMessage()));
    }

    public static ResponseEntity<ErrorResponse> of(HttpStatus httpStatus, Errors errors) {
        return ResponseEntity
            .status(httpStatus)
            .body(new ErrorResponse(errors.getCode(), errors.getMessage()));
    }
}
