package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.MoneyRequest;
import com.example.enterprise_digital_wallet.dto.WalletResponse;

import java.util.UUID;

public interface WalletService {

    WalletResponse getWalletByUserId(UUID userId);

    WalletResponse deposit(UUID userId, MoneyRequest request);

    WalletResponse withdraw(UUID userId, MoneyRequest request);
}