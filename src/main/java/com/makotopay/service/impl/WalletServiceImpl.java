package com.makotopay.service.impl;

import com.makotopay.dto.request.AddMoneyRequest;
import com.makotopay.dto.request.TransferRequest;
import com.makotopay.dto.response.TransactionResponse;
import com.makotopay.dto.response.WalletResponse;
import com.makotopay.entity.FraudLog;
import com.makotopay.entity.Transaction;
import com.makotopay.entity.User;
import com.makotopay.entity.Wallet;
import com.makotopay.enums.TransactionStatus;
import com.makotopay.enums.TransactionType;
import com.makotopay.exception.BadRequestException;
import com.makotopay.exception.ResourceNotFoundException;
import com.makotopay.repository.FraudLogRepository;
import com.makotopay.repository.TransactionRepository;
import com.makotopay.repository.UserRepository;
import com.makotopay.repository.WalletRepository;
import com.makotopay.service.IdempotencyService;
import com.makotopay.service.RateLimitService;
import com.makotopay.service.WalletService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final FraudLogRepository fraudLogRepository;
    private final IdempotencyService idempotencyService;
    private final RateLimitService rateLimitService;

    public WalletServiceImpl(
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            FraudLogRepository fraudLogRepository,
            IdempotencyService idempotencyService,
            RateLimitService rateLimitService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.fraudLogRepository = fraudLogRepository;
        this.idempotencyService = idempotencyService;
        this.rateLimitService = rateLimitService;
    }

    // ✅ Balance Check
    @Override
    public WalletResponse getBalance(String email) {
        User user = getUser(email);
        Wallet wallet = getWallet(user);
        return mapToWalletResponse(wallet);
    }

    // ✅ Add Money
    @Override
    @Transactional
    public WalletResponse addMoney(String email,
                                    AddMoneyRequest request) {
        User user = getUser(email);
        Wallet wallet = getWallet(user);

        // Balance update చేయడం
        wallet.setBalance(
            wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        // Transaction record చేయడం
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .receiver(user)
                .amount(request.getAmount())
                .type(TransactionType.CREDIT)
                .status(TransactionStatus.SUCCESS)
                .description("Money added to wallet")
                .build();
        transactionRepository.save(transaction);

        return mapToWalletResponse(wallet);
    }

    // ✅ Transfer Money — Redis Idempotency + Rate Limiting
    @Override
    @Transactional
    public TransactionResponse transfer(String email,
                                         TransferRequest request) {

        // ✅ Redis Idempotency Check
        if (idempotencyService.isDuplicate(
                request.getIdempotencyKey())) {
            throw new BadRequestException(
                "Duplicate transaction! " +
                "Same idempotency key already used.");
        }

        // ✅ Redis Rate Limit Check
        if (rateLimitService.isRateLimitExceeded(email)) {
            throw new BadRequestException(
                "Too many transfers! " +
                "Maximum 5 transfers per 2 minutes. " +
                "Please wait.");
        }

        // Sender తనకు తాను transfer చేసుకోకుండా
        if (email.equals(request.getReceiverEmail())) {
            throw new BadRequestException(
                "Cannot transfer money to yourself!");
        }

        User sender = getUser(email);
        User receiver = getUser(request.getReceiverEmail());

        Wallet senderWallet = getWallet(sender);
        Wallet receiverWallet = getWallet(receiver);

        // Balance check చేయడం
        if (senderWallet.getBalance()
                .compareTo(request.getAmount()) < 0) {
            throw new BadRequestException(
                "Insufficient balance! Available: "
                + senderWallet.getBalance());
        }

        // Transaction create చేయడం — PENDING గా
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .sender(sender)
                .receiver(receiver)
                .amount(request.getAmount())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .idempotencyKey(request.getIdempotencyKey())
                .build();

        try {
            // Sender balance minus చేయడం
            senderWallet.setBalance(
                senderWallet.getBalance()
                    .subtract(request.getAmount()));
            walletRepository.save(senderWallet);

            // Receiver balance plus చేయడం
            receiverWallet.setBalance(
                receiverWallet.getBalance()
                    .add(request.getAmount()));
            walletRepository.save(receiverWallet);

            // Transaction SUCCESS గా mark చేయడం
            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            // ✅ Redis లో Idempotency Key Save చేయడం
            idempotencyService.saveKey(
                request.getIdempotencyKey());

            // ✅ Redis లో Rate Limit Increment చేయడం
            rateLimitService.incrementTransferCount(email);

        } catch (Exception e) {
            // ఏదైనా fail అయితే — FAILED గా mark చేయడం
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            // Fraud log చేయడం
            FraudLog fraudLog = FraudLog.builder()
                    .user(sender)
                    .reason("Transfer failed: " + e.getMessage())
                    .attemptCount(1)
                    .build();
            fraudLogRepository.save(fraudLog);

            throw new BadRequestException(
                "Transfer failed! Amount will be reversed.");
        }

        return mapToTransactionResponse(transaction);
    }

    // ✅ Transaction History — Pagination + Filter
    @Override
    public Page<TransactionResponse> getTransactionHistory(
            String email, int page, int size, String type) {

        User user = getUser(email);
        Pageable pageable = PageRequest.of(
            page, size, Sort.by("createdAt").descending());

        Page<Transaction> transactions;

        if (type != null && !type.isEmpty()) {
            TransactionType transactionType =
                TransactionType.valueOf(type.toUpperCase());
            transactions = transactionRepository
                .findBySenderOrReceiverAndType(
                    user, user, transactionType, pageable);
        } else {
            transactions = transactionRepository
                .findBySenderOrReceiver(user, user, pageable);
        }

        return transactions.map(this::mapToTransactionResponse);
    }

    // ── Helper Methods ──────────────────────────────────────

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found: " + email));
    }

    private Wallet getWallet(User user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Wallet not found for user: "
                    + user.getEmail()));
    }

    private WalletResponse mapToWalletResponse(Wallet wallet) {
        return WalletResponse.builder()
                .walletId(wallet.getId())
                .ownerName(wallet.getUser().getFullName())
                .ownerEmail(wallet.getUser().getEmail())
                .balance(wallet.getBalance())
                .active(wallet.isActive())
                .build();
    }

    private TransactionResponse mapToTransactionResponse(
            Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .senderEmail(transaction.getSender() != null
                    ? transaction.getSender().getEmail() : null)
                .receiverEmail(transaction.getReceiver() != null
                    ? transaction.getReceiver().getEmail() : null)
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}