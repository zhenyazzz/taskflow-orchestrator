package org.example.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.userservice.dto.request.CreateUserRequest;
import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.response.UserResponse;
import org.example.userservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management operations")
public class UserController {
    private final UserService userService;

    @GetMapping("/all")
    @Operation(summary = "Get users", description = "Returns a list of users")
    public ResponseEntity<List<UserResponse>> getAllUsersList() {
        log.info("Getting all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        log.info("Getting user by id: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest userRequest) {
        log.info("Creating user: {}", userRequest.username());
        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest userRequest) {
        log.info("Updating user with id {}: {}", id, userRequest);
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
} 