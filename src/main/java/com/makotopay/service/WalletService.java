package com.makotopay.service;

import com.makotopay.dto.request.AddMoneyRequest;
import com.makotopay.dto.request.TransferRequest;
import com.makotopay.dto.response.TransactionResponse;
import com.makotopay.dto.response.WalletResponse;
import org.springframework.data.domain.Page;

public interface WalletService {
    WalletResponse getBalance(String email);
    WalletResponse addMoney(String email, AddMoneyRequest request);
    TransactionResponse transfer(String email, TransferRequest request);
    Page<TransactionResponse> getTransactionHistory(
        String email, int page, int size, String type);
}