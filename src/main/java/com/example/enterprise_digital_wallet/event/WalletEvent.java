package com.example.enterprise_digital_wallet.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletEvent(
        String eventType,
        UUID transactionId,
        UUID senderUserId,
        UUID receiverUserId,
        BigDecimal amount,
        String currency,
        String referenceNumber,
        Instant occurredAt
) {
}