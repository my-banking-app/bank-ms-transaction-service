package com.mybankingapp.transactionservices.service.impl;

import com.mybankingapp.transactionservices.dto.DepositRequest;
import com.mybankingapp.transactionservices.dto.TransferRequest;
import com.mybankingapp.transactionservices.dto.WithdrawalRequest;
import com.mybankingapp.transactionservices.enums.TransactionStatus;
import com.mybankingapp.transactionservices.enums.TransactionType;
import com.mybankingapp.transactionservices.model.Transaction;
import com.mybankingapp.transactionservices.repository.TransactionRepository;
import com.mybankingapp.transactionservices.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction createDeposit(DepositRequest request) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.getAccountId());
        transaction.setToAccountId(null);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction createWithdrawal(WithdrawalRequest request) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.getAccountId());
        transaction.setToAccountId(null);
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction createTransfer(TransferRequest request) {
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(request.getFromAccountId());
        transaction.setToAccountId(request.getToAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setType(TransactionType.TRANSFER_INTERNAL); // O TRANSFER_EXTERNAL si aplica
        transaction.setStatus(TransactionStatus.COMPLETED);
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findByFromAccountIdOrToAccountId(accountId, accountId);
    }

    @Override
    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));
    }
}
