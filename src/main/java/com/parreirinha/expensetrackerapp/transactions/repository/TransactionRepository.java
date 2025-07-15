package com.parreirinha.expensetrackerapp.transactions.repository;

import java.util.List;
import java.util.UUID;

import com.parreirinha.expensetrackerapp.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.parreirinha.expensetrackerapp.transactions.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUser(User user);
    void deleteByUser(User user);
}
