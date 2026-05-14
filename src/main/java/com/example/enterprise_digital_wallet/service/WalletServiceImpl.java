package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.MoneyRequest;
import com.example.enterprise_digital_wallet.dto.WalletResponse;
import com.example.enterprise_digital_wallet.entity.Wallet;
import com.example.enterprise_digital_wallet.exception.InsufficientBalanceException;
import com.example.enterprise_digital_wallet.exception.ResourceNotFoundException;
import com.example.enterprise_digital_wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.enterprise_digital_wallet.entity.TransactionStatus;
import com.example.enterprise_digital_wallet.entity.TransactionType;
import com.example.enterprise_digital_wallet.entity.WalletTransaction;
import com.example.enterprise_digital_wallet.repository.TransactionRepository;
import com.example.enterprise_digital_wallet.event.WalletEvent;
import java.time.Instant;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    private final TransactionRepository transactionRepository;

    private final WalletEventProducer walletEventProducer;

    @Override
    public WalletResponse getWalletByUserId(UUID userId) {
        Wallet wallet = getWallet(userId);
        return mapToWalletResponse(wallet);
    }

    @Override
    @Transactional
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

        transactionRepository.save(transaction);

        WalletEvent event = new WalletEvent(
                "MONEY_DEPOSITED",
                transaction.getId(),
                null,
                savedWallet.getUser().getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getReferenceNumber(),
                Instant.now()
        );

        walletEventProducer.publishWalletEvent(event);

        return mapToWalletResponse(savedWallet);
    }

    @Override
    @Transactional
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

        transactionRepository.save(transaction);

        WalletEvent event = new WalletEvent(
                "MONEY_WITHDRAWN",
                transaction.getId(),
                savedWallet.getUser().getId(),
                null,
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getReferenceNumber(),
                Instant.now()
        );

        walletEventProducer.publishWalletEvent(event);

        return mapToWalletResponse(savedWallet);
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