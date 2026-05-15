package com.example.enterprise_digital_wallet.controller;

import com.example.enterprise_digital_wallet.dto.ApiResponse;
import com.example.enterprise_digital_wallet.search.TransactionSearchDocument;
import com.example.enterprise_digital_wallet.search.TransactionSearchService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionSearchController {

    private final TransactionSearchService transactionSearchService;

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/reference/{referenceNumber}")
    public ApiResponse<List<TransactionSearchDocument>> searchByReferenceNumber(
            @PathVariable String referenceNumber
    ) {
        return ApiResponse.success(
                "Transactions fetched by reference number",
                transactionSearchService.searchByReferenceNumber(referenceNumber)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/users/{userId}")
    public ApiResponse<List<TransactionSearchDocument>> searchByUserId(
            @PathVariable UUID userId
    ) {
        return ApiResponse.success(
                "Transactions fetched by user ID",
                transactionSearchService.searchByUserId(userId)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/type/{transactionType}")
    public ApiResponse<List<TransactionSearchDocument>> searchByTransactionType(
            @PathVariable String transactionType
    ) {
        return ApiResponse.success(
                "Transactions fetched by transaction type",
                transactionSearchService.searchByTransactionType(transactionType)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/status/{status}")
    public ApiResponse<List<TransactionSearchDocument>> searchByStatus(
            @PathVariable String status
    ) {
        return ApiResponse.success(
                "Transactions fetched by status",
                transactionSearchService.searchByStatus(status)
        );
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/amount")
    public ApiResponse<List<TransactionSearchDocument>> searchByAmountRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount
    ) {
        return ApiResponse.success(
                "Transactions fetched by amount range",
                transactionSearchService.searchByAmountRange(minAmount, maxAmount)
        );
    }
}