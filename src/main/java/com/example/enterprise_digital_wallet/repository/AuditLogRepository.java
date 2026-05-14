package com.example.enterprise_digital_wallet.repository;

import com.example.enterprise_digital_wallet.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}