package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.TransactionResponse;
import com.example.enterprise_digital_wallet.dto.TransferRequest;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponse transfer(TransferRequest request);

    List<TransactionResponse> getTransactionsByUserId(UUID userId);

    TransactionResponse getTransactionById(UUID transactionId);
}