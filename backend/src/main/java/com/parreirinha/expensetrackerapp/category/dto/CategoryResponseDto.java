package com.parreirinha.expensetrackerapp.category.dto;

import java.util.UUID;

public record CategoryResponseDto(
        UUID id,
        String name
) {}
