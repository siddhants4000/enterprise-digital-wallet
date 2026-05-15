package com.example.enterprise_digital_wallet.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionSearchRepository extends ElasticsearchRepository<TransactionSearchDocument, String> {

    List<TransactionSearchDocument> findByReferenceNumberContainingIgnoreCase(String referenceNumber);

    List<TransactionSearchDocument> findBySenderUserIdOrReceiverUserId(UUID senderUserId, UUID receiverUserId);

    List<TransactionSearchDocument> findByTransactionType(String transactionType);
}