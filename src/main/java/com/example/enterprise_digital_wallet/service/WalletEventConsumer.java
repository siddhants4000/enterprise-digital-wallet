package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.entity.AuditLog;
import com.example.enterprise_digital_wallet.event.WalletEvent;
import com.example.enterprise_digital_wallet.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletEventConsumer {

    private final AuditLogRepository auditLogRepository;

    @KafkaListener(
            topics = "${app.kafka.topics.wallet-events}",
            groupId = "wallet-audit-service",
            containerFactory = "walletEventKafkaListenerContainerFactory"
    )
    public void consumeWalletEvent(WalletEvent event) {
        AuditLog auditLog = AuditLog.builder()
                .eventType(event.eventType())
                .transactionId(event.transactionId())
                .senderUserId(event.senderUserId())
                .receiverUserId(event.receiverUserId())
                .amount(event.amount())
                .currency(event.currency())
                .referenceNumber(event.referenceNumber())
                .occurredAt(event.occurredAt())
                .build();

        auditLogRepository.save(auditLog);
    }
}