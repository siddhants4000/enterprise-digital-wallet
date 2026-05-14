package com.example.enterprise_digital_wallet.repository;

import com.example.enterprise_digital_wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query("SELECT w FROM Wallet w JOIN FETCH w.user WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdWithUser(UUID userId);
}