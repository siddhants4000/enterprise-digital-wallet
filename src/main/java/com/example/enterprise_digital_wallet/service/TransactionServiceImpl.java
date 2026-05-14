package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.TransactionResponse;
import com.example.enterprise_digital_wallet.dto.TransferRequest;
import com.example.enterprise_digital_wallet.entity.TransactionStatus;
import com.example.enterprise_digital_wallet.entity.TransactionType;
import com.example.enterprise_digital_wallet.entity.Wallet;
import com.example.enterprise_digital_wallet.entity.WalletTransaction;
import com.example.enterprise_digital_wallet.exception.InsufficientBalanceException;
import com.example.enterprise_digital_wallet.exception.InvalidTransactionException;
import com.example.enterprise_digital_wallet.exception.ResourceNotFoundException;
import com.example.enterprise_digital_wallet.repository.TransactionRepository;
import com.example.enterprise_digital_wallet.repository.WalletRepository;
import com.example.enterprise_digital_wallet.event.WalletEvent;
import java.time.Instant;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final String DEFAULT_CURRENCY = "EUR";

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletEventProducer walletEventProducer;

    @Override
    @Transactional
    @CacheEvict(value = "wallets", allEntries = true)
    public TransactionResponse transfer(TransferRequest request) {
        validateTransferRequest(request);

        return transactionRepository.findByIdempotencyKey(request.idempotencyKey())
                .map(this::mapToTransactionResponse)
                .orElseGet(() -> processTransfer(request));
    }

    @Override
    public List<TransactionResponse> getTransactionsByUserId(UUID userId) {
        return transactionRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToTransactionResponse)
                .toList();
    }

    @Override
    public TransactionResponse getTransactionById(UUID transactionId) {
        WalletTransaction transaction = transactionRepository.findByIdWithWalletsAndUsers(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        return mapToTransactionResponse(transaction);
    }

    private TransactionResponse processTransfer(TransferRequest request) {
        Wallet senderWallet = getWallet(request.senderUserId());
        Wallet receiverWallet = getWallet(request.receiverUserId());

        if (senderWallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(request.amount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(request.amount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .senderWallet(senderWallet)
                .receiverWallet(receiverWallet)
                .amount(request.amount())
                .currency(DEFAULT_CURRENCY)
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .referenceNumber(generateReferenceNumber())
                .idempotencyKey(request.idempotencyKey())
                .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);

        WalletEvent event = new WalletEvent(
                "MONEY_TRANSFERRED",
                savedTransaction.getId(),
                senderWallet.getUser().getId(),
                receiverWallet.getUser().getId(),
                savedTransaction.getAmount(),
                savedTransaction.getCurrency(),
                savedTransaction.getReferenceNumber(),
                Instant.now()
        );

        walletEventProducer.publishWalletEvent(event);

        return mapToTransactionResponse(savedTransaction);
    }

    private void validateTransferRequest(TransferRequest request) {
        if (request.senderUserId().equals(request.receiverUserId())) {
            throw new InvalidTransactionException("Sender and receiver cannot be same");
        }
    }

    private Wallet getWallet(UUID userId) {
        return walletRepository.findByUserIdWithUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user"));
    }

    private TransactionResponse mapToTransactionResponse(WalletTransaction transaction) {
        UUID senderUserId = transaction.getSenderWallet() == null
                ? null
                : transaction.getSenderWallet().getUser().getId();

        UUID receiverUserId = transaction.getReceiverWallet() == null
                ? null
                : transaction.getReceiverWallet().getUser().getId();

        return new TransactionResponse(
                transaction.getId(),
                senderUserId,
                receiverUserId,
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getTransactionType(),
                transaction.getStatus(),
                transaction.getReferenceNumber(),
                transaction.getCreatedAt()
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