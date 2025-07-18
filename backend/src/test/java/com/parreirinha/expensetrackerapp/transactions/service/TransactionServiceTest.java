package com.parreirinha.expensetrackerapp.transactions.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class TransactionServiceTest {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private TransactionMapper transactionMapper;
    private TransactionService transactionService;

    @BeforeEach
    void setup() {
        transactionRepository = mock(TransactionRepository.class);
        userRepository = mock(UserRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        transactionMapper = mock(TransactionMapper.class);
        transactionService = new TransactionService(transactionRepository, userRepository, categoryRepository, transactionMapper);
    }

    @Test
    void createTransaction_ShouldSaveTransaction() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        TransactionRequestDto dto = new TransactionRequestDto(BigDecimal.TEN, categoryId, TransactionType.INCOME, LocalDate.now());
        Transaction transaction = new Transaction();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(transactionMapper.toTransaction(dto)).thenReturn(transaction);
        // Act
        transactionService.createTransaction(username, dto);
        // Assert
        verify(transactionRepository).save(transaction);
        assertThat(transaction.getUser()).isEqualTo(user);
        assertThat(transaction.getCategory()).isEqualTo(category);
    }

    @Test
    void getTransactions_ShouldReturnTransactionResponseDtoList() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        List<TransactionResponseDto> dtos = Arrays.asList(
            new TransactionResponseDto(UUID.randomUUID(), BigDecimal.TEN, null, TransactionType.INCOME.name(), LocalDate.now()),
            new TransactionResponseDto(UUID.randomUUID(), BigDecimal.ONE, null, TransactionType.EXPENSE.name(), LocalDate.now())
        );
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(transactionRepository.findByUser(user)).thenReturn(transactions);
        when(transactionMapper.toTransactionResponseDtoList(transactions)).thenReturn(dtos);
        // Act
        List<TransactionResponseDto> result = transactionService.getTransactions(username);
        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void getTransaction_ShouldReturnTransactionResponseDto_WhenUserOwnsTransaction() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        Transaction transaction = new Transaction(); transaction.setUser(user);
        UUID id = UUID.randomUUID();
        TransactionResponseDto dto = new TransactionResponseDto(id, BigDecimal.TEN, null, TransactionType.INCOME.name(), LocalDate.now());
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(transactionMapper.toTransactionResponseDto(transaction)).thenReturn(dto);
        // Act
        TransactionResponseDto result = transactionService.getTransaction(username, id);
        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void getTransaction_ShouldThrowForbiddenException_WhenUserDoesNotOwnTransaction() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername("other");
        Transaction transaction = new Transaction(); transaction.setUser(user);
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        // Act & Assert
        assertThatThrownBy(() -> transactionService.getTransaction(username, id))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void updateTransaction_ShouldUpdateTransaction_WhenUserOwnsTransaction() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        Transaction transaction = new Transaction(); transaction.setUser(user);
        UUID id = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        Category category = new Category();
        TransactionRequestDto dto = new TransactionRequestDto(BigDecimal.ONE, categoryId, TransactionType.EXPENSE, LocalDate.now());
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        // Act
        transactionService.updateTransaction(id, username, dto);
        // Assert
        verify(transactionRepository).save(transaction);
        assertThat(transaction.getAmount()).isEqualTo(BigDecimal.ONE);
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getType()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    void updateTransaction_ShouldThrowForbiddenException_WhenUserDoesNotOwnTransaction() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername("other");
        Transaction transaction = new Transaction(); transaction.setUser(user);
        UUID id = UUID.randomUUID();
        TransactionRequestDto dto = new TransactionRequestDto(BigDecimal.ONE, null, TransactionType.EXPENSE, LocalDate.now());
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        // Act & Assert
        assertThatThrownBy(() -> transactionService.updateTransaction(id, username, dto))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deleteTransaction_ShouldDeleteTransaction_WhenUserOwnsTransaction() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        Transaction transaction = new Transaction(); transaction.setUser(user);
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        // Act
        transactionService.deleteTransaction(id, username);
        // Assert
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransaction_ShouldThrowForbiddenException_WhenUserDoesNotOwnTransaction() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername("other");
        Transaction transaction = new Transaction(); transaction.setUser(user);
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        // Act & Assert
        assertThatThrownBy(() -> transactionService.deleteTransaction(id, username))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getBalance_ShouldReturnCorrectBalance() {
        // Arrange
        String username = "miguel";
        User user = new User(); user.setUsername(username);
        Transaction t1 = new Transaction(); t1.setType(TransactionType.INCOME); t1.setAmount(BigDecimal.TEN);
        Transaction t2 = new Transaction(); t2.setType(TransactionType.EXPENSE); t2.setAmount(BigDecimal.valueOf(3));
        List<Transaction> transactions = Arrays.asList(t1, t2);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(transactionRepository.findByUser(user)).thenReturn(transactions);
        // Act
        BigDecimal balance = transactionService.getBalance(username);
        // Assert
        assertThat(balance).isEqualByComparingTo(BigDecimal.valueOf(7));
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));
        // Act
        Transaction result = transactionService.getTransactionById(id);
        // Assert
        assertThat(result).isEqualTo(transaction);
    }

    @Test
    void getTransactionById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());
        // Act & Assert
        assertThatThrownBy(() -> transactionService.getTransactionById(id))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getTransactionsByUser_ShouldReturnList() {
        // Arrange
        User user = new User();
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.findByUser(user)).thenReturn(transactions);
        // Act
        List<Transaction> result = transactionService.getTransactionsByUser(user);
        // Assert
        assertThat(result).hasSize(2);
    }
} 