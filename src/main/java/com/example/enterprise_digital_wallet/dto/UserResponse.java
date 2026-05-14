package com.example.enterprise_digital_wallet.dto;

import com.example.enterprise_digital_wallet.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String phoneNumber,
        UserStatus status,
        Instant createdAt
) {
}