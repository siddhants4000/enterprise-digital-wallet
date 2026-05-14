package com.example.enterprise_digital_wallet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(

        @NotNull(message = "Sender user ID is required")
        UUID senderUserId,

        @NotNull(message = "Receiver user ID is required")
        UUID receiverUserId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Idempotency key is required")
        String idempotencyKey
) {
}