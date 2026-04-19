package com.makotopay.controller;

import com.makotopay.dto.request.AddMoneyRequest;
import com.makotopay.dto.request.TransferRequest;
import com.makotopay.dto.response.ApiResponse;
import com.makotopay.dto.response.TransactionResponse;
import com.makotopay.dto.response.WalletResponse;
import com.makotopay.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@Tag(name = "Wallet", description = "Wallet Management APIs")
@SecurityRequirement(name = "Bearer Auth")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    @Operation(summary = "Get wallet balance",
               description = "Returns current wallet balance")
    public ResponseEntity<ApiResponse<WalletResponse>> getBalance(
            @AuthenticationPrincipal UserDetails userDetails) {
        WalletResponse response = walletService
            .getBalance(userDetails.getUsername());
        return ResponseEntity.ok(
            ApiResponse.success("Balance fetched!", response));
    }

    @PostMapping("/add-money")
    @Operation(summary = "Add money to wallet",
               description = "Add money to your wallet")
    public ResponseEntity<ApiResponse<WalletResponse>> addMoney(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddMoneyRequest request) {
        WalletResponse response = walletService
            .addMoney(userDetails.getUsername(), request);
        return ResponseEntity.ok(
            ApiResponse.success(
                "Money added successfully!", response));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money",
               description = "Transfer money to another user " +
               "with Idempotency protection")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse response = walletService
            .transfer(userDetails.getUsername(), request);
        return ResponseEntity.ok(
            ApiResponse.success("Transfer successful!", response));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Transaction history",
               description = "Get paginated transaction history " +
               "with optional type filter")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>>
            getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        Page<TransactionResponse> response = walletService
            .getTransactionHistory(
                userDetails.getUsername(), page, size, type);
        return ResponseEntity.ok(
            ApiResponse.success("Transactions fetched!", response));
    }
}