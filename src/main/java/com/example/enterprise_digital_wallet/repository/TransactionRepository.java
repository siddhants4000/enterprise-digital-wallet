package com.example.enterprise_digital_wallet.repository;

import com.example.enterprise_digital_wallet.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<WalletTransaction, UUID> {

    @Query("""
            SELECT t FROM WalletTransaction t
            LEFT JOIN FETCH t.senderWallet sw
            LEFT JOIN FETCH sw.user
            LEFT JOIN FETCH t.receiverWallet rw
            LEFT JOIN FETCH rw.user
            WHERE sw.user.id = :userId OR rw.user.id = :userId
            ORDER BY t.createdAt DESC
            """)
    List<WalletTransaction> findAllByUserId(UUID userId);

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