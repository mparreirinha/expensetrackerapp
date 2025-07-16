package com.parreirinha.expensetrackerapp.transactions.controller;

import com.parreirinha.expensetrackerapp.transactions.dto.TransactionRequestDto;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionResponseDto;
import com.parreirinha.expensetrackerapp.transactions.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequestMapping("/transactions")
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping()
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getTransactions(userDetails.getUsername()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                                 @PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransaction(userDetails.getUsername(), id));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Void> createTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestBody TransactionRequestDto dto) {
        transactionService.createTransaction(userDetails.getUsername(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable UUID id,
                                                  @RequestBody TransactionRequestDto dto) {
        transactionService.updateTransaction(id, userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@AuthenticationPrincipal UserDetails userDetails,
                                                  @PathVariable UUID id) {
        transactionService.deleteTransaction(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.getBalance(userDetails.getUsername()));
    }

}
