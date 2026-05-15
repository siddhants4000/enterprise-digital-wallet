package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.AuditLogResponse;
import com.example.enterprise_digital_wallet.entity.AuditLog;
import com.example.enterprise_digital_wallet.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AuditLogResponse> getAuditLogsByTransactionId(UUID transactionId) {
        return auditLogRepository.findByTransactionIdOrderByConsumedAtDesc(transactionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return new AuditLogResponse(
                auditLog.getId(),
                auditLog.getEventType(),
                auditLog.getTransactionId(),
                auditLog.getSenderUserId(),
                auditLog.getReceiverUserId(),
                auditLog.getAmount(),
                auditLog.getCurrency(),
                auditLog.getReferenceNumber(),
                auditLog.getOccurredAt(),
                auditLog.getConsumedAt()
        );
    }
}