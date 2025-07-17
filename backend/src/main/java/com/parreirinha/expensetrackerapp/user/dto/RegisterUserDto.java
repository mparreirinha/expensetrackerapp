package com.parreirinha.expensetrackerapp.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserDto(
    @Schema(example = "user")
    @NotBlank(message = "Username is required")
    String username,
    @Schema(example = "user@email.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    @Schema(example = "password")
    @NotBlank(message = "Password is required")
    String password
) {}
