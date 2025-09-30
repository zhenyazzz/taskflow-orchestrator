package org.example.authservice.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp
) {
    public static ErrorResponse of(HttpStatus status, String message) {
        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                Instant.now()
        );
    }
}

