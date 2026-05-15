package com.example.enterprise_digital_wallet.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        String eventType,
        UUID transactionId,
        UUID senderUserId,
        UUID receiverUserId,
        BigDecimal amount,
        String currency,
        String referenceNumber,
        Instant occurredAt,
        Instant consumedAt
) {
}