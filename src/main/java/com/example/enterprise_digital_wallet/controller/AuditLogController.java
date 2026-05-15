package com.example.enterprise_digital_wallet.controller;

import com.example.enterprise_digital_wallet.dto.ApiResponse;
import com.example.enterprise_digital_wallet.dto.AuditLogResponse;
import com.example.enterprise_digital_wallet.service.AuditLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<List<AuditLogResponse>> getAllAuditLogs() {
        List<AuditLogResponse> response = auditLogService.getAllAuditLogs();
        return ApiResponse.success("Audit logs fetched successfully", response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/transactions/{transactionId}")
    public ApiResponse<List<AuditLogResponse>> getAuditLogsByTransactionId(@PathVariable UUID transactionId) {
        List<AuditLogResponse> response = auditLogService.getAuditLogsByTransactionId(transactionId);
        return ApiResponse.success("Audit logs fetched successfully", response);
    }
}