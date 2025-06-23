package com.projeto.tiajuda.dto.response;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
        String message,
        int status,
        String error,
        String path,
        Instant timestamp
) {

    public ErrorResponse(String message, HttpStatus status, String path) {
        this(message, status.value(), status.getReasonPhrase(), path, Instant.now());
    }

}
