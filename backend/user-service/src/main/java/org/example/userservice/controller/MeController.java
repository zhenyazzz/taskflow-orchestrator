package org.example.userservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.example.userservice.dto.request.UpdateUserRequest;
import org.example.userservice.dto.response.ProfileResponse;
import org.example.userservice.model.UserDetailsImpl;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/profile")
@RequiredArgsConstructor
@Tag(name = "Me", description = "Operations for current authenticated user")
public class MeController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get my profile", description = "Returns profile of the authenticated user")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        System.out.println("Fetching profile for UUID: " + principal.getUUID());
        ProfileResponse profile = userService.getMyProfile(principal.getUUID());
        System.out.println("Profile fetched: " + profile);
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Update my profile", description = "Updates profile of the authenticated user")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateMyProfile(principal.getUUID(), request));
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Delete my profile", description = "Delete profile of the authenticated user")
    public ResponseEntity<Void> deleteMyProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        userService.deleteMyProfile(principal.getUUID());
        return ResponseEntity.noContent().build();
    }
}
