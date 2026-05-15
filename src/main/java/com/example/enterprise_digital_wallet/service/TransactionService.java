package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.TransactionResponse;
import com.example.enterprise_digital_wallet.dto.TransferRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {

    TransactionResponse transfer(TransferRequest request);

    Page<TransactionResponse> getTransactionsByUserId(UUID userId, Pageable pageable);

    TransactionResponse getTransactionById(UUID transactionId);
}