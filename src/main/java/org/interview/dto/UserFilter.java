package org.interview.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Filter criteria for searching users")
public record UserFilter(

        @Schema(description = "Filter by name (partial match, case-insensitive)", example = "John")
        String name,

        @Schema(description = "Filter by exact email", example = "john@example.com")
        String email,

        @Schema(description = "Filter by active status", example = "true")
        Boolean active,

        @Schema(description = "Filter users created after this date")
        LocalDateTime createdAfter,

        @Schema(description = "Filter users created before this date")
        LocalDateTime createdBefore,

        @Schema(description = "Filter by minimum age", example = "18")
        Integer minAge,

        @Schema(description = "Filter by maximum age", example = "65")
        Integer maxAge
) {}