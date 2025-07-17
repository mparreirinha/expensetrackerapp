package com.parreirinha.expensetrackerapp.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginUserDto(
    @Schema(example = "user")
    @NotBlank(message = "Username is required")
    String username,
    @Schema(example = "password")
    @NotBlank(message = "Password is required")
    String password
) {}
