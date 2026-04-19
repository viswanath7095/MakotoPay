package com.makotopay.service;

import com.makotopay.dto.request.LoginRequest;
import com.makotopay.dto.request.RegisterRequest;
import com.makotopay.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}