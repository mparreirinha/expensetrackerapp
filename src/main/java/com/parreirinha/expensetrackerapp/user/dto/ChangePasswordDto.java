package com.parreirinha.expensetrackerapp.user.dto;

public record ChangePasswordDto(
    String oldPassword,
    String newPassword
) {}
