package com.parreirinha.expensetrackerapp.transactions.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.parreirinha.expensetrackerapp.transactions.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}
