package org.interview.dto.response;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "User information response")
public record UserResponse(

        @Schema(description = "User's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        String id,

        @Schema(description = "User's full name", example = "John Doe")
        String name,

        @Schema(description = "User's email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "User's phone number", example = "+1234567890")
        String phoneNumber,

        @Schema(description = "User's date of birth", example = "1990-01-15")
        LocalDate dateOfBirth,

        @Schema(description = "User's age calculated from date of birth", example = "33")
        Integer age,

        @Schema(description = "Whether the user is active", example = "true")
        Boolean active,

        @Schema(description = "When the user was created", example = "2024-01-01T10:00:00")
        LocalDateTime createdAt,

        @Schema(description = "When the user was last updated", example = "2024-01-15T14:30:00")
        LocalDateTime updatedAt
) {}