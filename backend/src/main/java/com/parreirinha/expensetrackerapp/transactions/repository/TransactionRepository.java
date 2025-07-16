package com.parreirinha.expensetrackerapp.transactions.repository;

import java.util.List;
import java.util.UUID;

import com.parreirinha.expensetrackerapp.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parreirinha.expensetrackerapp.transactions.domain.Transaction;
import com.parreirinha.expensetrackerapp.category.domain.Category;


public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByUser(User user);
    void deleteByUser(User user);
    @Modifying
    @Query("UPDATE Transaction t SET t.category = null WHERE t.category = :category")
    void unsetCategoryFromTransactions(@Param("category") Category category);
}
