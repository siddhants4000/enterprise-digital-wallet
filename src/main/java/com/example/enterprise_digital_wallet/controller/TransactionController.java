package com.example.enterprise_digital_wallet.controller;

import com.example.enterprise_digital_wallet.dto.ApiResponse;
import com.example.enterprise_digital_wallet.dto.TransactionResponse;
import com.example.enterprise_digital_wallet.dto.TransferRequest;
import com.example.enterprise_digital_wallet.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ApiResponse<TransactionResponse> transfer(@Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ApiResponse.success("Transfer completed successfully", response);
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<List<TransactionResponse>> getTransactionsByUserId(@PathVariable UUID userId) {
        List<TransactionResponse> response = transactionService.getTransactionsByUserId(userId);
        return ApiResponse.success("Transactions fetched successfully", response);
    }

    @GetMapping("/{transactionId}")
    public ApiResponse<TransactionResponse> getTransactionById(@PathVariable UUID transactionId) {
        TransactionResponse response = transactionService.getTransactionById(transactionId);
        return ApiResponse.success("Transaction fetched successfully", response);
    }
}