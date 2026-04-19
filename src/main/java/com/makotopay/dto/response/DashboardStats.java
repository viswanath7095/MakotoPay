package com.makotopay.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {

    private long totalUsers;
    private long totalTransactions;
    private BigDecimal totalMoneyTransferred;
    private long activeUsers;
    private long blockedUsers;
    private long successTransactions;
    private long failedTransactions;
}