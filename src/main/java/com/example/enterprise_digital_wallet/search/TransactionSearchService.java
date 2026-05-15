package com.example.enterprise_digital_wallet.search;

import com.example.enterprise_digital_wallet.entity.WalletTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionSearchService {

    private final TransactionSearchRepository transactionSearchRepository;

    public void indexTransaction(WalletTransaction transaction) {
        UUID senderUserId = transaction.getSenderWallet() == null
                ? null
                : transaction.getSenderWallet().getUser().getId();

        UUID receiverUserId = transaction.getReceiverWallet() == null
                ? null
                : transaction.getReceiverWallet().getUser().getId();

        TransactionSearchDocument document = TransactionSearchDocument.builder()
                .id(transaction.getId().toString())
                .transactionId(transaction.getId())
                .senderUserId(senderUserId)
                .receiverUserId(receiverUserId)
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .transactionType(transaction.getTransactionType().name())
                .status(transaction.getStatus().name())
                .referenceNumber(transaction.getReferenceNumber())
                .createdAt(transaction.getCreatedAt())
                .build();

        transactionSearchRepository.save(document);
    }

    public List<TransactionSearchDocument> searchByReferenceNumber(String referenceNumber) {
        return transactionSearchRepository.findByReferenceNumberContainingIgnoreCase(referenceNumber);
    }

    public List<TransactionSearchDocument> searchByUserId(UUID userId) {
        return transactionSearchRepository.findBySenderUserIdOrReceiverUserId(userId, userId);
    }

    public List<TransactionSearchDocument> searchByTransactionType(String transactionType) {
        return transactionSearchRepository.findByTransactionType(transactionType);
    }
}