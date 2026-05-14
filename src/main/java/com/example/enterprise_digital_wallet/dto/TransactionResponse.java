package com.example.enterprise_digital_wallet.dto;

import com.example.enterprise_digital_wallet.entity.TransactionStatus;
import com.example.enterprise_digital_wallet.entity.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID senderUserId,
        UUID receiverUserId,
        BigDecimal amount,
        String currency,
        TransactionType transactionType,
        TransactionStatus status,
        String referenceNumber,
        Instant createdAt
) {
}