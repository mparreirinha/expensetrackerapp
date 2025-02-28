package com.parreirinha.expensetrackerapp.auth.dto;

public record LoginResponseDto(
    String token,
    long expiresIn
) {}
