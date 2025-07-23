package com.parreirinha.expensetrackerapp.auth.dto;

public record LoginResponseDto(
    String token,
    String refreshToken,
    long expiresIn
) {}
