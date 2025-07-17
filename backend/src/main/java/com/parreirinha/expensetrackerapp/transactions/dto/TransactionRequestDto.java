package com.parreirinha.expensetrackerapp.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.parreirinha.expensetrackerapp.transactions.domain.TransactionType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record TransactionRequestDto(
        @Schema(example = "100.00")
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID categoryId,
        @Schema(example = "INCOME")
        @NotNull(message = "Transaction type is required")
        TransactionType type,
        @Schema(example = "2025-07-16")
        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date
) {}

