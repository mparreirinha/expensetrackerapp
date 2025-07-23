package com.parreirinha.expensetrackerapp.user.dto;

import java.util.UUID;

public record UserAdminResponseDto(
        UUID id,
        String username,
        String email
) {}
