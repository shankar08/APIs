package org.interview.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.interview.dto.UserFilter;
import org.interview.dto.request.CreateUserRequest;
import org.interview.dto.request.UpdateUserRequest;
import org.interview.dto.response.ErrorResponse;
import org.interview.dto.response.PageResponse;
import org.interview.dto.response.UserResponse;
import org.interview.exception.ResourceNotFoundException;
import org.interview.service.UserService;
import org.interview.util.SortParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;
    private final SortParser sortParser;


    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with this email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.create(request);

        return ResponseEntity
                .created(URI.create("/api/v1/users/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a single user by their unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable String id) {

        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Operation(
            summary = "List all users",
            description = "Retrieves a paginated list of all users"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort criteria (field,direction)", example = "name,asc")
            @RequestParam(required = false) String sort) {

        Pageable pageable = PageRequest.of(page, size, sortParser.parse(sort));
        Page<UserResponse> users = userService.findAll(pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.getTotalElements()))
                .body(PageResponse.of(users));
    }

    @Operation(
            summary = "Search users",
            description = "Search users with various filter criteria"
    )
    @GetMapping("/search")
    public ResponseEntity<PageResponse<UserResponse>> searchUsers(
            @Parameter(description = "Filter by name (partial match)")
            @RequestParam(required = false) String name,

            @Parameter(description = "Filter by email")
            @RequestParam(required = false) String email,

            @Parameter(description = "Filter by active status")
            @RequestParam(required = false) Boolean active,

            @Parameter(description = "Filter users created after this date")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,

            @Parameter(description = "Filter users created before this date")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore,

            @Parameter(description = "Filter by minimum age")
            @RequestParam(required = false) Integer minAge,

            @Parameter(description = "Filter by maximum age")
            @RequestParam(required = false) Integer maxAge,

            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort criteria", example = "name,asc")
            @RequestParam(required = false) String sort) {

        UserFilter filter = new UserFilter(name, email, active, createdAfter, createdBefore, minAge, maxAge);
        Pageable pageable = PageRequest.of(page, size, sortParser.parse(sort));

        Page<UserResponse> users = userService.search(filter, pageable);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.getTotalElements()))
                .body(PageResponse.of(users));
    }

    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID")
            @PathVariable String id,

            @Valid @RequestBody UpdateUserRequest request) {

        return userService.update(id, request)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by their ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable String id) {

        boolean deleted = userService.delete(id);

        if (!deleted) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        return ResponseEntity.noContent().build();
    }
}