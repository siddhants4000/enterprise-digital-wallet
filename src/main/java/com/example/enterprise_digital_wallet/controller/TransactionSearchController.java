package com.example.enterprise_digital_wallet.controller;

import com.example.enterprise_digital_wallet.search.TransactionSearchDocument;
import com.example.enterprise_digital_wallet.search.TransactionSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/search/transactions")
@RequiredArgsConstructor
public class TransactionSearchController {

    private final TransactionSearchService transactionSearchService;

    @GetMapping("/reference")
    public List<TransactionSearchDocument> searchByReferenceNumber(@RequestParam String referenceNumber) {
        return transactionSearchService.searchByReferenceNumber(referenceNumber);
    }

    @GetMapping("/user/{userId}")
    public List<TransactionSearchDocument> searchByUserId(@PathVariable UUID userId) {
        return transactionSearchService.searchByUserId(userId);
    }

    @GetMapping("/type/{transactionType}")
    public List<TransactionSearchDocument> searchByTransactionType(@PathVariable String transactionType) {
        return transactionSearchService.searchByTransactionType(transactionType);
    }
}