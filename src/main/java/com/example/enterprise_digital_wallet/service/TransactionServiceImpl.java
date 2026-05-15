package com.example.enterprise_digital_wallet.service;

import com.example.enterprise_digital_wallet.dto.TransactionResponse;
import com.example.enterprise_digital_wallet.dto.TransferRequest;
import com.example.enterprise_digital_wallet.entity.TransactionStatus;
import com.example.enterprise_digital_wallet.entity.TransactionType;
import com.example.enterprise_digital_wallet.entity.Wallet;
import com.example.enterprise_digital_wallet.entity.WalletTransaction;
import com.example.enterprise_digital_wallet.event.WalletEvent;
import com.example.enterprise_digital_wallet.exception.InsufficientBalanceException;
import com.example.enterprise_digital_wallet.exception.InvalidTransactionException;
import com.example.enterprise_digital_wallet.exception.ResourceNotFoundException;
import com.example.enterprise_digital_wallet.repository.TransactionRepository;
import com.example.enterprise_digital_wallet.repository.WalletRepository;
import com.example.enterprise_digital_wallet.search.TransactionSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private static final String DEFAULT_CURRENCY = "EUR";

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletEventProducer walletEventProducer;
    private final TransactionSearchService transactionSearchService;

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
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getTransactionsByUserId(UUID userId, Pageable pageable) {
        Page<WalletTransaction> transactions = transactionRepository.findAllByUserId(userId, pageable);

        return transactions.map(this::mapToTransactionResponse);
    }

    @Override
    @Transactional(readOnly = true)
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

        Wallet savedSenderWallet = walletRepository.save(senderWallet);
        Wallet savedReceiverWallet = walletRepository.save(receiverWallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .senderWallet(savedSenderWallet)
                .receiverWallet(savedReceiverWallet)
                .amount(request.amount())
                .currency(DEFAULT_CURRENCY)
                .transactionType(TransactionType.TRANSFER)
                .status(TransactionStatus.SUCCESS)
                .referenceNumber(generateReferenceNumber())
                .idempotencyKey(request.idempotencyKey())
                .build();

        WalletTransaction savedTransaction = transactionRepository.save(transaction);

        transactionSearchService.indexTransaction(savedTransaction);

        WalletEvent event = new WalletEvent(
                "MONEY_TRANSFERRED",
                savedTransaction.getId(),
                savedSenderWallet.getUser().getId(),
                savedReceiverWallet.getUser().getId(),
                savedTransaction.getAmount(),
                savedTransaction.getCurrency(),
                savedTransaction.getReferenceNumber(),
                savedTransaction.getCreatedAt()
        );

        publishWalletEventSafely(event);

        return mapToTransactionResponse(savedTransaction);
    }

    private void publishWalletEventSafely(WalletEvent event) {
        try {
            walletEventProducer.publishWalletEvent(event);
        } catch (Exception exception) {
            System.out.println("Kafka publish failed: " + exception.getMessage());
        }
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