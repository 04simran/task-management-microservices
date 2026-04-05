package com.optipace.auth.service.Impl;

import com.optipace.auth.dto.AuthResponse;
import com.optipace.auth.dto.LoginRequest;
import com.optipace.auth.dto.RegisterRequest;
import com.optipace.auth.entity.RefreshToken;
import com.optipace.auth.entity.User;
import com.optipace.auth.exception.BadRequestException;
import com.optipace.auth.repository.UserRepository;
import com.optipace.auth.security.JwtUtil;
import com.optipace.auth.service.AuthService;
import com.optipace.auth.service.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service

public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "USER";
        }
        if (!role.equals("USER") && !role.equals("ADMIN")) {
            throw new BadRequestException("Invalid role. Must be USER or ADMIN");
        }
        user.setRole(role);

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        String accessToken = jwtUtil.generateToken(
                user.getId().toString(), user.getUsername(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username or password");
        }

        log.info("User logged in: {}", user.getUsername());

        String accessToken = jwtUtil.generateToken(
                user.getId().toString(), user.getUsername(), user.getRole());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        String accessToken = jwtUtil.generateToken(
                user.getId().toString(), user.getUsername(), user.getRole());

        log.info("Access token refreshed for user: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
