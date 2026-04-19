package com.makotopay.repository;

import com.makotopay.entity.Transaction;
import com.makotopay.entity.User;
import com.makotopay.enums.TransactionStatus;
import com.makotopay.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    Page<Transaction> findBySenderOrReceiver(
        User sender, User receiver, Pageable pageable);

    Page<Transaction> findBySenderOrReceiverAndType(
        User sender, User receiver,
        TransactionType type, Pageable pageable);

    // Admin కోసం కొత్త method
    long countByStatus(TransactionStatus status);
}