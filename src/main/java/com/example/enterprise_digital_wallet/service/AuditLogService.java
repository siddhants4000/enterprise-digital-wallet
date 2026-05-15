package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.AuditLogResponse;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {

    List<AuditLogResponse> getAllAuditLogs();

    List<AuditLogResponse> getAuditLogsByTransactionId(UUID transactionId);
}