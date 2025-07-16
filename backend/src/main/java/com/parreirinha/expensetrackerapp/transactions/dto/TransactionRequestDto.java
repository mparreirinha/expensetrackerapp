package com.parreirinha.expensetrackerapp.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequestDto(
        BigDecimal amount,
        UUID categoryId,
        String type,
        LocalDate date
) {}
