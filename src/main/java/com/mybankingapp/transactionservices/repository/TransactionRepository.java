package com.mybankingapp.transactionservices.repository;

import com.mybankingapp.transactionservices.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByFromAccountIdOrToAccountId(UUID fromAccountId, UUID toAccountId);
}
