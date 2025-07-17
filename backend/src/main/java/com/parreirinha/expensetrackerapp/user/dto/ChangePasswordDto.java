package com.parreirinha.expensetrackerapp.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
    @Schema(example = "oldPassword")
    @NotBlank(message = "Old Password is required")
    String oldPassword,
    @Schema(example = "newPassword")
    @NotBlank(message = "New Password is required")
    String newPassword
) {}
