package com.parreirinha.expensetrackerapp.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDto(
     @Schema(example = "category")
     @NotBlank(message = "Name is required")
     String name
) {}
