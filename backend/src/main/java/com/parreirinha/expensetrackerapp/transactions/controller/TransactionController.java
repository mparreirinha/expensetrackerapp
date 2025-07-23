package com.parreirinha.expensetrackerapp.transactions.controller;

import com.parreirinha.expensetrackerapp.transactions.dto.TransactionRequestDto;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionResponseDto;
import com.parreirinha.expensetrackerapp.transactions.service.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(
    name = "Transactions",
    description = "Endpoints for managing user's financial transactions and balance"
)
@RequestMapping("/transactions")
@Validated
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(transactionService.getTransactions(jwt.getClaimAsString("preferred_username")));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getTransaction(@AuthenticationPrincipal Jwt jwt,
                                                                 @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(transactionService.getTransaction(jwt.getClaimAsString("preferred_username"), id));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> createTransaction(@AuthenticationPrincipal Jwt jwt,
                                                  @RequestBody @Valid TransactionRequestDto dto) {
        transactionService.createTransaction(jwt.getClaimAsString("preferred_username"), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTransaction(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable @NotNull UUID id,
                                                  @RequestBody @Valid TransactionRequestDto dto) {
        transactionService.updateTransaction(id, jwt.getClaimAsString("preferred_username"), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@AuthenticationPrincipal Jwt jwt,
                                                  @PathVariable @NotNull UUID id) {
        transactionService.deleteTransaction(id, jwt.getClaimAsString("preferred_username"));
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(transactionService.getBalance(jwt.getClaimAsString("preferred_username")));
    }

}
