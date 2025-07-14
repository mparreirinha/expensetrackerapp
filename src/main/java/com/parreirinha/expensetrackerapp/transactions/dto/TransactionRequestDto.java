package com.parreirinha.expensetrackerapp.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequestDto(
        BigDecimal amount,
        String category,
        String type,
        LocalDate date
) {}
