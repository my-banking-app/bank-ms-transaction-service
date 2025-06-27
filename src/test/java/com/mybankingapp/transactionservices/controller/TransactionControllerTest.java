package com.mybankingapp.transactionservices.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybankingapp.transactionservices.dto.DepositRequest;
import com.mybankingapp.transactionservices.dto.TransferRequest;
import com.mybankingapp.transactionservices.dto.WithdrawalRequest;
import com.mybankingapp.transactionservices.enums.TransactionStatus;
import com.mybankingapp.transactionservices.enums.TransactionType;
import com.mybankingapp.transactionservices.model.Transaction;
import com.mybankingapp.transactionservices.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link TransactionController}.
 */
@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    /* ---------- helpers ---------- */

    private Transaction stubTransaction(TransactionType type) {
        Transaction tx = new Transaction();
        tx.setId(UUID.randomUUID());
        tx.setFromAccountId(UUID.randomUUID());
        tx.setToAccountId(UUID.randomUUID());
        tx.setAmount(BigDecimal.valueOf(100));
        tx.setType(type);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setTimestamp(LocalDateTime.now());
        tx.setDescription("Unit-test transaction");
        return tx;
    }

    /* ---------- POST /deposit ---------- */

    @Test
    @DisplayName("POST /deposit – crea depósito y devuelve 200 OK")
    void deposit() throws Exception {
        Transaction tx = stubTransaction(TransactionType.DEPOSIT);
        Mockito.when(transactionService.createDeposit(any(DepositRequest.class))).thenReturn(tx);

        DepositRequest body = new DepositRequest();
        body.setAccountId(tx.getFromAccountId());
        body.setAmount(tx.getAmount());
        body.setDescription("Test deposit");

        mockMvc.perform(post("/api/v1/transactions/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(tx.getId().toString())))
                .andExpect(jsonPath("$.type", is(TransactionType.DEPOSIT.name())));
    }

    /* ---------- POST /withdraw ---------- */

    @Test
    @DisplayName("POST /withdraw – crea retiro y devuelve 200 OK")
    void withdraw() throws Exception {
        Transaction tx = stubTransaction(TransactionType.WITHDRAWAL);
        Mockito.when(transactionService.createWithdrawal(any(WithdrawalRequest.class))).thenReturn(tx);

        WithdrawalRequest body = new WithdrawalRequest();
        body.setAccountId(tx.getFromAccountId());
        body.setAmount(tx.getAmount());
        body.setDescription("Test withdrawal");

        mockMvc.perform(post("/api/v1/transactions/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is(TransactionType.WITHDRAWAL.name())));
    }

    /* ---------- POST /transfer ---------- */

    @Test
    @DisplayName("POST /transfer – crea transferencia y devuelve 200 OK")
    void transfer() throws Exception {
        Transaction tx = stubTransaction(TransactionType.TRANSFER_INTERNAL);
        Mockito.when(transactionService.createTransfer(any(TransferRequest.class))).thenReturn(tx);

        TransferRequest body = new TransferRequest();
        body.setFromAccountId(tx.getFromAccountId());
        body.setToAccountId(tx.getToAccountId());
        body.setAmount(tx.getAmount());
        body.setDescription("Test transfer");

        mockMvc.perform(post("/api/v1/transactions/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is(TransactionType.TRANSFER_INTERNAL.name())));
    }

    /* ---------- GET /account/{accountId} ---------- */

    @Test
    @DisplayName("GET /account/{accountId} – lista transacciones de la cuenta")
    void getTransactionsByAccount() throws Exception {
        Transaction tx = stubTransaction(TransactionType.DEPOSIT);
        UUID accountId = tx.getFromAccountId();

        Mockito.when(transactionService.getTransactionsByAccountId(accountId)).thenReturn(List.of(tx));

        mockMvc.perform(get("/api/v1/transactions/account/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fromAccountId", is(accountId.toString())));
    }

    /* ---------- GET /{id} ---------- */

    @Test
    @DisplayName("GET /{id} – devuelve transacción por id")
    void getById() throws Exception {
        Transaction tx = stubTransaction(TransactionType.DEPOSIT);
        UUID id = tx.getId();

        Mockito.when(transactionService.getTransactionById(id)).thenReturn(tx);

        mockMvc.perform(get("/api/v1/transactions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));
    }
}
