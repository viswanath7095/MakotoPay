package com.makotopay.controller;

import com.makotopay.dto.response.ApiResponse;
import com.makotopay.dto.response.DashboardStats;
import com.makotopay.dto.response.TransactionResponse;
import com.makotopay.entity.User;
import com.makotopay.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin Management APIs")
@SecurityRequirement(name = "Bearer Auth")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users",
               description = "Admin — paginated users list")
    public ResponseEntity<ApiResponse<Page<User>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> users = adminService.getAllUsers(page, size);
        return ResponseEntity.ok(
            ApiResponse.success("Users fetched!", users));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get all transactions",
               description = "Admin — all transactions paginated")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>>
            getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionResponse> transactions =
            adminService.getAllTransactions(page, size);
        return ResponseEntity.ok(
            ApiResponse.success(
                "Transactions fetched!", transactions));
    }

    @PutMapping("/block-user/{userId}")
    @Operation(summary = "Block user",
               description = "Admin — block suspicious user")
    public ResponseEntity<ApiResponse<String>> blockUser(
            @PathVariable Long userId) {
        String result = adminService.blockUser(userId);
        return ResponseEntity.ok(
            ApiResponse.success(result, null));
    }

    @PutMapping("/unblock-user/{userId}")
    @Operation(summary = "Unblock user",
               description = "Admin — unblock user")
    public ResponseEntity<ApiResponse<String>> unblockUser(
            @PathVariable Long userId) {
        String result = adminService.unblockUser(userId);
        return ResponseEntity.ok(
            ApiResponse.success(result, null));
    }

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Dashboard statistics",
               description = "Admin — complete system statistics")
    public ResponseEntity<ApiResponse<DashboardStats>> getStats() {
        DashboardStats stats = adminService.getDashboardStats();
        return ResponseEntity.ok(
            ApiResponse.success("Stats fetched!", stats));
    }
}