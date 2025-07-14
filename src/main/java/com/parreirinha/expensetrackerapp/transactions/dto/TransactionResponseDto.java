package com.parreirinha.expensetrackerapp.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDto(
        UUID id,
        BigDecimal amount,
        String category,
        String type,
        LocalDate date
) {}
