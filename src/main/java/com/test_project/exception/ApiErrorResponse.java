package com.test_project.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, ?> details
) {
    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return of(status, message, path, null);
    }

    public static ApiErrorResponse of(
            HttpStatus status,
            String message,
            String path,
            Map<String, ?> details
    ) {
        return new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                details
        );
    }
}
