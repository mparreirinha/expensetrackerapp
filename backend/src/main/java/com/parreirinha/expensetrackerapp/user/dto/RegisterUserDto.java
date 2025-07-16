package com.parreirinha.expensetrackerapp.user.dto;

public record RegisterUserDto(
    String username, 
    String email, 
    String password
) {}
