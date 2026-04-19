package com.makotopay.service.impl;

import com.makotopay.dto.request.LoginRequest;
import com.makotopay.dto.request.RegisterRequest;
import com.makotopay.dto.response.AuthResponse;
import com.makotopay.entity.User;
import com.makotopay.entity.Wallet;
import com.makotopay.enums.UserRole;
import com.makotopay.exception.BadRequestException;
import com.makotopay.exception.ResourceNotFoundException;
import com.makotopay.repository.UserRepository;
import com.makotopay.repository.WalletRepository;
import com.makotopay.security.JwtUtil;
import com.makotopay.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           WalletRepository walletRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Email already exists గా check చేయడం
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                "Email already registered: " + request.getEmail());
        }

        // Phone already exists గా check చేయడం
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException(
                "Phone number already registered!");
        }

        // User create చేయడం
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        // Wallet automatically create చేయడం
        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .build();

        walletRepository.save(wallet);

        // JWT Token generate చేయడం
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole().name())
                .message("Registration successful!")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // User exists గా check చేయడం
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "User not found with email: " + request.getEmail()));

        // Password check చేయడం
        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password!");
        }

        // Active user గా check చేయడం
        if (!user.isActive()) {
            throw new BadRequestException(
                "Account is blocked. Contact support!");
        }

        // JWT Token generate చేయడం
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .message("Login successful!")
                .build();
    }
}