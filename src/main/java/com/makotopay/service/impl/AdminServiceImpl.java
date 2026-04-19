package com.makotopay.service.impl;

import com.makotopay.dto.response.DashboardStats;
import com.makotopay.dto.response.TransactionResponse;
import com.makotopay.entity.Transaction;
import com.makotopay.entity.User;
import com.makotopay.enums.TransactionStatus;
import com.makotopay.exception.ResourceNotFoundException;
import com.makotopay.repository.TransactionRepository;
import com.makotopay.repository.UserRepository;
import com.makotopay.repository.WalletRepository;
import com.makotopay.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    public AdminServiceImpl(
            UserRepository userRepository,
            TransactionRepository transactionRepository,
            WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    // ✅ All Users — Pagination తో
    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(
            page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable);
    }

    // ✅ All Transactions — Pagination తో
    @Override
    public Page<TransactionResponse> getAllTransactions(
            int page, int size) {
        Pageable pageable = PageRequest.of(
            page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactions =
            transactionRepository.findAll(pageable);
        return transactions.map(this::mapToTransactionResponse);
    }

    // ✅ User Block చేయడం
    @Override
    public String blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found with id: " + userId));

        if (!user.isActive()) {
            throw new RuntimeException(
                "User is already blocked!");
        }

        user.setActive(false);
        userRepository.save(user);
        return "User blocked successfully: " + user.getEmail();
    }

    // ✅ User Unblock చేయడం
    @Override
    public String unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found with id: " + userId));

        if (user.isActive()) {
            throw new RuntimeException(
                "User is already active!");
        }

        user.setActive(true);
        userRepository.save(user);
        return "User unblocked successfully: " + user.getEmail();
    }

    // ✅ Dashboard Stats
    @Override
    public DashboardStats getDashboardStats() {

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByActive(true);
        long blockedUsers = userRepository.countByActive(false);

        long totalTransactions = transactionRepository.count();
        long successTransactions = transactionRepository
            .countByStatus(TransactionStatus.SUCCESS);
        long failedTransactions = transactionRepository
            .countByStatus(TransactionStatus.FAILED);

        BigDecimal totalMoneyTransferred = transactionRepository
            .findAll()
            .stream()
            .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardStats.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .blockedUsers(blockedUsers)
                .totalTransactions(totalTransactions)
                .successTransactions(successTransactions)
                .failedTransactions(failedTransactions)
                .totalMoneyTransferred(totalMoneyTransferred)
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