package com.parreirinha.expensetrackerapp.transactions.service;

import com.parreirinha.expensetrackerapp.category.domain.Category;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
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

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public void createTransaction(String username, TransactionRequestDto dto) {
        User user = getUserByUsername(username);
        Category category = null;
        if (dto.categoryId() != null)
           category = getCategoryById(dto.categoryId());
        Transaction transaction = transactionMapper.toTransaction(dto);
        transaction.setCategory(category);
        transaction.setUser(user);
        transactionRepository.save(transaction);
    }

    public List<TransactionResponseDto> getTransactions(String username) {
        User user = getUserByUsername(username);
        return transactionMapper.toTransactionResponseDtoList(getTransactionsByUser(user));
    }

    public TransactionResponseDto getTransaction(String username, UUID id) {
        Transaction transaction = getTransactionById(id);
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have access to this transaction");
        return transactionMapper.toTransactionResponseDto(transaction);
    }

    @Transactional
    public void updateTransaction(UUID id, String username, TransactionRequestDto dto) {
        Transaction transaction = getTransactionById(id);
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have permission to update this transaction");
        Category category = null;
        if (dto.categoryId() != null)
            category = getCategoryById(dto.categoryId());
        transaction.setAmount(dto.amount());
        transaction.setCategory(category);
        transaction.setType(dto.type());
        transaction.setDate(dto.date());
        transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(UUID id, String username) {
        Transaction transaction = getTransactionById(id);
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have permission to delete this transaction");
        transactionRepository.delete(transaction);
    }

    public BigDecimal getBalance(String username) {
        User user = getUserByUsername(username);
        List<Transaction> transactions = getTransactionsByUser(user);
        return transactions.stream()
                .map(t -> t.getType() == TransactionType.INCOME ?
                        t.getAmount() : t.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }

    public List<Transaction> getTransactionsByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
