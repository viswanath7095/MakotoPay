package com.makotopay.service;

import com.makotopay.dto.response.DashboardStats;
import com.makotopay.dto.response.TransactionResponse;
import com.makotopay.dto.response.WalletResponse;
import com.makotopay.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminService {
    Page<User> getAllUsers(int page, int size);
    Page<TransactionResponse> getAllTransactions(int page, int size);
    String blockUser(Long userId);
    String unblockUser(Long userId);
    DashboardStats getDashboardStats();
}