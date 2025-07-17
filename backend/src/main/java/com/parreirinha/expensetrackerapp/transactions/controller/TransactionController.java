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
import org.springframework.security.core.userdetails.UserDetails;
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
    @Operation(summary = "Get all transactions", description = "Returns a list of all user transactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping()
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactions(userDetails.getUsername()));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get transaction by ID", description = "Returns a specific transaction by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access to this transaction is forbidden", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(transactionService.getTransaction(userDetails.getUsername(), id));
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new transaction", description = "Creates a new transaction for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping
    public ResponseEntity<Void> createTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody @Valid TransactionRequestDto dto) {
        transactionService.createTransaction(userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update an existing transaction", description = "Updates a specific transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transaction updated successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied to this transaction", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Transaction or category not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable @NotNull UUID id,
                                                  @RequestBody @Valid TransactionRequestDto dto) {
        transactionService.updateTransaction(id, userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a transaction", description = "Deletes a specific transaction by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transaction deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied to delete this transaction", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "Transaction not found", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable @NotNull UUID id) {
        transactionService.deleteTransaction(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get balance", description = "Calculates and returns the user's total balance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance calculated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getBalance(userDetails.getUsername()));
    }

}
