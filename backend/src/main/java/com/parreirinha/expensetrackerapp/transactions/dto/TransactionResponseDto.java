package com.parreirinha.expensetrackerapp.transactions.dto;

import com.parreirinha.expensetrackerapp.category.dto.CategoryResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDto(
        UUID id,
        BigDecimal amount,
        CategoryResponseDto category,
        String type,
        LocalDate date
) {}
