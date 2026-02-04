package org.interview.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Error response")
public record ErrorResponse(

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Error type", example = "Bad Request")
        String error,

        @Schema(description = "Error message", example = "Validation failed")
        String message,

        @Schema(description = "Request path", example = "/api/v1/users")
        String path,

        @Schema(description = "Timestamp of the error")
        LocalDateTime timestamp,

        @Schema(description = "List of field validation errors")
        List<FieldError> fieldErrors
) {
    public record FieldError(String field, String message) {}
}