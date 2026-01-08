package com.example.os.api.exception;

import java.time.Instant;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private final Instant timestamp;
    private final int status;
    private final String message;
    private final String details;

    public ErrorResponse(int status, String message, String details) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.details = details;
    }
}
