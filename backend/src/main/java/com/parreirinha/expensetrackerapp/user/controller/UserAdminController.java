package com.parreirinha.expensetrackerapp.user.controller;

import com.parreirinha.expensetrackerapp.user.dto.UserAdminResponseDto;
import com.parreirinha.expensetrackerapp.user.service.UserAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "User Administration",description = "Admin endpoints to manage users")
@RequestMapping("/admin/users")
@Validated
@RestController
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all users (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UserAdminResponseDto>> getUsers() {
        return ResponseEntity.ok(userAdminService.getUsers());
    }


    @Operation(summary = "Get user by ID", description = "Returns a user by their ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminResponseDto> getUser(@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(userAdminService.getUser(id));
    }

    @Operation(summary = "Delete user", description = "Deletes a user by ID (Admin only). Cannot delete Admin users.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "403", description = "Forbidden (cannot delete Admin users)", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotNull UUID id) {
        userAdminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
