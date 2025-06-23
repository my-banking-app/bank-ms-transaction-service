package com.mybankingapp.transactionservices.service;

import com.mybankingapp.transactionservices.dto.DepositRequest;
import com.mybankingapp.transactionservices.dto.TransferRequest;
import com.mybankingapp.transactionservices.dto.WithdrawalRequest;
import com.mybankingapp.transactionservices.model.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    Transaction createDeposit(DepositRequest request);

    Transaction createWithdrawal(WithdrawalRequest request);

    Transaction createTransfer(TransferRequest request);

    List<Transaction> getTransactionsByAccountId(UUID accountId);

    Transaction getTransactionById(UUID id);
}
