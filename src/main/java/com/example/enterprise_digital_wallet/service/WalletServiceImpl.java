package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.MoneyRequest;
import com.example.enterprise_digital_wallet.dto.WalletResponse;
import com.example.enterprise_digital_wallet.entity.TransactionStatus;
import com.example.enterprise_digital_wallet.entity.TransactionType;
import com.example.enterprise_digital_wallet.entity.Wallet;
import com.example.enterprise_digital_wallet.entity.WalletTransaction;
import com.example.enterprise_digital_wallet.event.WalletEvent;
import com.example.enterprise_digital_wallet.exception.InsufficientBalanceException;
import com.example.enterprise_digital_wallet.exception.ResourceNotFoundException;
import com.example.enterprise_digital_wallet.repository.TransactionRepository;
import com.example.enterprise_digital_wallet.repository.WalletRepository;
import com.example.enterprise_digital_wallet.search.TransactionSearchService;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletEventProducer walletEventProducer;
    private final TransactionSearchService transactionSearchService;

    @Override
    @Cacheable(value = "wallets", key = "#userId")
    public WalletResponse getWalletByUserId(UUID userId) {
        Wallet wallet = getWallet(userId);
        return mapToWalletResponse(wallet);
    }

    @Override
    @Transactional
    @CacheEvict(value = "wallets", key = "#userId")
    public WalletResponse deposit(UUID userId, MoneyRequest request) {
        Wallet wallet = getWallet(userId);
        wallet.setBalance(wallet.getBalance().add(request.amount()));

        Wallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .receiverWallet(savedWallet)
                .amount(request.amount())
                .currency(savedWallet.getCurrency())
                .transactionType(TransactionType.DEPOSIT)
                .status(TransactionStatus.SUCCESS)
                .referenceNumber(generateReferenceNumber())
                .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);
        transactionSearchService.indexTransaction(savedTransaction);

        WalletEvent event = new WalletEvent(
                "MONEY_DEPOSITED",
                savedTransaction.getId(),
                null,
                savedWallet.getUser().getId(),
                savedTransaction.getAmount(),
                savedTransaction.getCurrency(),
                savedTransaction.getReferenceNumber(),
                savedTransaction.getCreatedAt()
        );

        publishWalletEventSafely(event);

        return mapToWalletResponse(savedWallet);
    }

    @Override
    @Transactional
    @CacheEvict(value = "wallets", key = "#userId")
    public WalletResponse withdraw(UUID userId, MoneyRequest request) {
        Wallet wallet = getWallet(userId);

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(request.amount()));

        Wallet savedWallet = walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .senderWallet(savedWallet)
                .amount(request.amount())
                .currency(savedWallet.getCurrency())
                .transactionType(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.SUCCESS)
                .referenceNumber(generateReferenceNumber())
                .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);

        WalletEvent event = new WalletEvent(
                "MONEY_WITHDRAWN",
                savedTransaction.getId(),
                savedWallet.getUser().getId(),
                null,
                savedTransaction.getAmount(),
                savedTransaction.getCurrency(),
                savedTransaction.getReferenceNumber(),
                savedTransaction.getCreatedAt()
        );

        publishWalletEventSafely(event);

        return mapToWalletResponse(savedWallet);
    }

    private void publishWalletEventSafely(WalletEvent event) {
        try {
            walletEventProducer.publishWalletEvent(event);
        } catch (Exception exception) {
            System.out.println("Kafka publish failed: " + exception.getMessage());
        }
    }

    private Wallet getWallet(UUID userId) {
        return walletRepository.findByUserIdWithUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user"));
    }

    private WalletResponse mapToWalletResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUser().getId(),
                wallet.getUser().getEmail(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getCreatedAt()
        );
    }

    private String generateReferenceNumber() {
        return "TXN-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }
}