package com.makotopay.controller;

import com.makotopay.dto.request.LoginRequest;
import com.makotopay.dto.request.RegisterRequest;
import com.makotopay.dto.response.ApiResponse;
import com.makotopay.dto.response.AuthResponse;
import com.makotopay.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", 
     description = "Register & Login APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user",
               description = "Creates new user with wallet")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(
            ApiResponse.success(
                "User registered successfully!", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login user",
               description = "Returns JWT token on success")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
            ApiResponse.success("Login successful!", response));
    }
}