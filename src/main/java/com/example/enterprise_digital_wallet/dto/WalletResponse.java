package com.example.enterprise_digital_wallet.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WalletResponse (
        UUID id,
        UUID userId,
        String userEmail,
        BigDecimal balance,
        String currency,
        Instant createdAt
) implements Serializable {
}