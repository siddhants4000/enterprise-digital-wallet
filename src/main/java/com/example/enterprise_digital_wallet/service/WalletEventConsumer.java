package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.entity.AuditLog;
import com.example.enterprise_digital_wallet.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${app.kafka.topics.wallet-events}",
            groupId = "wallet-audit-service"
    )
    public void consumeWalletEvent(String message) throws Exception {
        System.out.println("Kafka event received: " + message);

        JsonNode node = objectMapper.readTree(message);

        AuditLog auditLog = AuditLog.builder()
                .eventType(node.get("eventType").asText())
                .transactionId(UUID.fromString(node.get("transactionId").asText()))
                .senderUserId(parseUuid(node.get("senderUserId")))
                .receiverUserId(parseUuid(node.get("receiverUserId")))
                .amount(new BigDecimal(node.get("amount").asText()))
                .currency(node.get("currency").asText())
                .referenceNumber(node.get("referenceNumber").asText())
                .occurredAt(parseInstant(node.get("occurredAt")))
                .consumedAt(Instant.now())
                .build();

        auditLogRepository.save(auditLog);

        System.out.println("Audit log saved for transaction: " + auditLog.getTransactionId());
    }

    private UUID parseUuid(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }

        return UUID.fromString(node.asText());
    }

    private Instant parseInstant(JsonNode node) {
        if (node == null || node.isNull()) {
            return Instant.now();
        }

        if (node.isTextual()) {
            return Instant.parse(node.asText());
        }

        BigDecimal epochSeconds = node.decimalValue();
        long seconds = epochSeconds.longValue();
        int nanos = epochSeconds.subtract(BigDecimal.valueOf(seconds))
                .movePointRight(9)
                .intValue();

        return Instant.ofEpochSecond(seconds, nanos);
    }
}