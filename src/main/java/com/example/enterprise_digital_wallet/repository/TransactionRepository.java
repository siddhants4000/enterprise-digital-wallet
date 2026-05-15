package com.example.enterprise_digital_wallet.repository;

import com.example.enterprise_digital_wallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    @Query("""
            SELECT t
            FROM WalletTransaction t
            LEFT JOIN t.senderWallet sw
            LEFT JOIN sw.user su
            LEFT JOIN t.receiverWallet rw
            LEFT JOIN rw.user ru
            WHERE su.id = :userId OR ru.id = :userId
            """)
    Page<WalletTransaction> findAllByUserId(UUID userId, Pageable pageable);

    @Query("""
            SELECT t FROM WalletTransaction t
            LEFT JOIN FETCH t.senderWallet sw
            LEFT JOIN FETCH sw.user
            LEFT JOIN FETCH t.receiverWallet rw
            LEFT JOIN FETCH rw.user
            WHERE t.id = :transactionId
            """)
    Optional<WalletTransaction> findByIdWithWalletsAndUsers(UUID transactionId);

    Optional<WalletTransaction> findByIdempotencyKey(String idempotencyKey);
}