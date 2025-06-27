package com.mybankingapp.transactionservices.service.impl;

import com.mybankingapp.transactionservices.dto.DepositRequest;
import com.mybankingapp.transactionservices.dto.TransferRequest;
import com.mybankingapp.transactionservices.dto.WithdrawalRequest;
import com.mybankingapp.transactionservices.enums.TransactionStatus;
import com.mybankingapp.transactionservices.enums.TransactionType;
import com.mybankingapp.transactionservices.model.Transaction;
import com.mybankingapp.transactionservices.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TransactionServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository repository;

    @InjectMocks
    private TransactionServiceImpl service;

    /* ------------- helpers ------------- */

    private static final BigDecimal AMOUNT = BigDecimal.valueOf(150);

    private void mockSaveReturnsInput() {
        when(repository.save(any(Transaction.class)))
                .thenAnswer(inv -> inv.getArgument(0));   // devuelve la entidad recibida
    }

    private DepositRequest depositReq() {
        DepositRequest r = new DepositRequest();
        r.setAccountId(UUID.randomUUID());
        r.setAmount(AMOUNT);
        r.setDescription("unit-deposit");
        return r;
    }

    private WithdrawalRequest withdrawalReq() {
        WithdrawalRequest r = new WithdrawalRequest();
        r.setAccountId(UUID.randomUUID());
        r.setAmount(AMOUNT);
        r.setDescription("unit-withdrawal");
        return r;
    }

    private TransferRequest transferReq() {
        TransferRequest r = new TransferRequest();
        r.setFromAccountId(UUID.randomUUID());
        r.setToAccountId(UUID.randomUUID());
        r.setAmount(AMOUNT);
        r.setDescription("unit-transfer");
        return r;
    }

    /* ------------- createDeposit ------------- */

    @Test
    @DisplayName("createDeposit debe persistir y devolver la transacción con tipo DEPOSIT")
    void createDeposit() {
        mockSaveReturnsInput();
        DepositRequest req = depositReq();

        Transaction result = service.createDeposit(req);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).save(captor.capture());
        Transaction saved = captor.getValue();

        assertThat(saved.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(saved.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(saved.getFromAccountId()).isEqualTo(req.getAccountId());
        assertThat(saved.getToAccountId()).isNull();
        assertThat(saved.getAmount()).isEqualByComparingTo(req.getAmount());
        assertThat(saved.getTimestamp()).isNotNull();
        assertThat(result).isSameAs(saved);      // se devolvió lo mismo que se guardó
    }

    /* ------------- createWithdrawal ------------- */

    @Test
    @DisplayName("createWithdrawal debe persistir y devolver la transacción con tipo WITHDRAWAL")
    void createWithdrawal() {
        mockSaveReturnsInput();
        WithdrawalRequest req = withdrawalReq();

        Transaction tx = service.createWithdrawal(req);

        assertThat(tx.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        verify(repository, times(1)).save(any(Transaction.class));
    }

    /* ------------- createTransfer ------------- */

    @Test
    @DisplayName("createTransfer debe persistir y devolver la transacción con tipo TRANSFER_INTERNAL")
    void createTransfer() {
        mockSaveReturnsInput();
        TransferRequest req = transferReq();

        Transaction tx = service.createTransfer(req);

        assertThat(tx.getType()).isEqualTo(TransactionType.TRANSFER_INTERNAL);
        assertThat(tx.getFromAccountId()).isEqualTo(req.getFromAccountId());
        assertThat(tx.getToAccountId()).isEqualTo(req.getToAccountId());
        verify(repository).save(any(Transaction.class));
    }

    /* ------------- getTransactionsByAccountId ------------- */

    @Test
    @DisplayName("getTransactionsByAccountId debe delegar en el repository y devolver la lista")
    void getTransactionsByAccountId() {
        UUID accId = UUID.randomUUID();
        Transaction sample = new Transaction();
        sample.setFromAccountId(accId);
        sample.setAmount(AMOUNT);
        sample.setType(TransactionType.DEPOSIT);
        sample.setStatus(TransactionStatus.COMPLETED);
        sample.setTimestamp(LocalDateTime.now());

        when(repository.findByFromAccountIdOrToAccountId(accId, accId))
                .thenReturn(List.of(sample));

        List<Transaction> result = service.getTransactionsByAccountId(accId);

        assertThat(result).hasSize(1).containsExactly(sample);
        verify(repository).findByFromAccountIdOrToAccountId(accId, accId);
    }

    /* ------------- getTransactionById (found) ------------- */

    @Test
    @DisplayName("getTransactionById devuelve la transacción cuando existe")
    void getTransactionById_found() {
        UUID id = UUID.randomUUID();
        Transaction tx = new Transaction();
        tx.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(tx));

        Transaction result = service.getTransactionById(id);

        assertThat(result).isSameAs(tx);
        verify(repository).findById(id);
    }

    /* ------------- getTransactionById (not found) ------------- */

    @Test
    @DisplayName("getTransactionById lanza RuntimeException cuando no existe")
    void getTransactionById_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTransactionById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(id.toString());

        verify(repository).findById(id);
    }
}
