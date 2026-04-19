package com.makotopay.repository;

import com.makotopay.entity.FraudLog;
import com.makotopay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudLogRepository 
        extends JpaRepository<FraudLog, Long> {

    List<FraudLog> findByUser(User user);
    long countByUser(User user);
}