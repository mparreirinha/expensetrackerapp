package com.parreirinha.expensetrackerapp.transactions.service;

import com.parreirinha.expensetrackerapp.exceptions.ForbiddenException;
import com.parreirinha.expensetrackerapp.exceptions.ResourceNotFoundException;
import com.parreirinha.expensetrackerapp.transactions.domain.Transaction;
import com.parreirinha.expensetrackerapp.transactions.domain.TransactionType;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionRequestDto;
import com.parreirinha.expensetrackerapp.transactions.dto.TransactionResponseDto;
import com.parreirinha.expensetrackerapp.transactions.mapper.TransactionMapper;
import com.parreirinha.expensetrackerapp.transactions.repository.TransactionRepository;
import com.parreirinha.expensetrackerapp.user.domain.User;
import com.parreirinha.expensetrackerapp.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createTransaction(String username, TransactionRequestDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Transaction transaction = TransactionMapper.INSTANCE.toTransaction(dto);
        transaction.setUser(user);
        transactionRepository.save(transaction);
    }

    public List<TransactionResponseDto> getTransactions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUser(user);
        return TransactionMapper.INSTANCE.toTransactionResponseDtoList(transactions);
    }

    public TransactionResponseDto getTransaction(String username, UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have access to this transaction");
        return TransactionMapper.INSTANCE.toTransactionResponseDto(transaction);
    }

    @Transactional
    public void updateTransaction(UUID id, String username, TransactionRequestDto dto) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have permission to update this transaction");
        transaction.setAmount(dto.amount());
        transaction.setCategory(dto.category());
        transaction.setType(TransactionType.valueOf(dto.type()));
        transaction.setDate(dto.date());
        transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(UUID id, String username) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have permission to delete this transaction");
        transactionRepository.delete(transaction);
    }

    public BigDecimal getBalance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUser(user);
        return transactions.stream()
                .map(t -> t.getType() == TransactionType.INCOME ?
                        t.getAmount() : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
