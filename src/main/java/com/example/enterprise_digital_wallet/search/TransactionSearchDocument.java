package com.example.enterprise_digital_wallet.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "wallet-transactions")
public class TransactionSearchDocument {

    @Id
    private String id;

    private UUID transactionId;

    private UUID senderUserId;

    private UUID receiverUserId;

    private BigDecimal amount;

    private String currency;

    private String transactionType;

    private String status;

    private String referenceNumber;

    private Instant createdAt;
}