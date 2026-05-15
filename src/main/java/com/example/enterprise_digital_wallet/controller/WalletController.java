package com.example.enterprise_digital_wallet.controller;

import com.example.enterprise_digital_wallet.dto.ApiResponse;
import com.example.enterprise_digital_wallet.dto.MoneyRequest;
import com.example.enterprise_digital_wallet.dto.WalletResponse;
import com.example.enterprise_digital_wallet.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WalletController {

    private final WalletService walletService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/{userId}")
    public ApiResponse<WalletResponse> getWalletByUserId(@PathVariable UUID userId) {
        WalletResponse response = walletService.getWalletByUserId(userId);
        return ApiResponse.success("Wallet fetched successfully", response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/users/{userId}/deposit")
    public ApiResponse<WalletResponse> deposit(
            @PathVariable UUID userId,
            @Valid @RequestBody MoneyRequest request
    ) {
        WalletResponse response = walletService.deposit(userId, request);
        return ApiResponse.success("Amount deposited successfully", response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/users/{userId}/withdraw")
    public ApiResponse<WalletResponse> withdraw(
            @PathVariable UUID userId,
            @Valid @RequestBody MoneyRequest request
    ) {
        WalletResponse response = walletService.withdraw(userId, request);
        return ApiResponse.success("Amount withdrawn successfully", response);
    }
}