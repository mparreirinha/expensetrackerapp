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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UserAdminResponseDto>> getUsers() {
        return ResponseEntity.ok(userAdminService.getUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserAdminResponseDto> getUser(@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(userAdminService.getUser(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotNull UUID id) {
        userAdminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
