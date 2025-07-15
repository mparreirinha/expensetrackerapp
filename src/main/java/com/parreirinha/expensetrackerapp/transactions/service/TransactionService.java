package com.parreirinha.expensetrackerapp.transactions.service;

import com.parreirinha.expensetrackerapp.category.domain.Category;
import com.parreirinha.expensetrackerapp.category.repository.CategoryRepository;
import com.parreirinha.expensetrackerapp.category.service.CategoryService;
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
import com.parreirinha.expensetrackerapp.user.service.UserQueryService;
import com.parreirinha.expensetrackerapp.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserQueryService userQueryService;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository,
                              UserQueryService userQueryService,
                              CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.userQueryService = userQueryService;
        this.categoryService = categoryService;
    }

    @Transactional
    public void createTransaction(String username, TransactionRequestDto dto) {
        User user = userQueryService.getUserByUsername(username);
        Category category = null;
        if (dto.categoryId() != null)
            category = categoryService.findCategoryById(dto.categoryId());
        Transaction transaction = TransactionMapper.INSTANCE.toTransaction(dto);
        transaction.setCategory(category);
        transaction.setUser(user);
        transactionRepository.save(transaction);
    }

    public List<TransactionResponseDto> getTransactions(String username) {
        User user = userQueryService.getUserByUsername(username);
        return TransactionMapper.INSTANCE.toTransactionResponseDtoList(getTransactionsByUser(user));
    }

    public TransactionResponseDto getTransaction(String username, UUID id) {
        Transaction transaction = getTransactionById(id);
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have access to this transaction");
        return TransactionMapper.INSTANCE.toTransactionResponseDto(transaction);
    }

    @Transactional
    public void updateTransaction(UUID id, String username, TransactionRequestDto dto) {
        Transaction transaction = getTransactionById(id);
        if (!transaction.getUser().getUsername().equals(username))
            throw new ForbiddenException("You do not have permission to update this transaction");
        Category category = null;
        if (dto.categoryId() != null)
            category = categoryService.findCategoryById(dto.categoryId());
        transaction.setAmount(dto.amount());
        transaction.setCategory(category);
        transaction.setType(TransactionType.valueOf(dto.type()));
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
        User user = userQueryService.getUserByUsername(username);
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

    @Transactional
    public void deleteByUser(User user) {
        transactionRepository.deleteByUser(user);
    }

}
