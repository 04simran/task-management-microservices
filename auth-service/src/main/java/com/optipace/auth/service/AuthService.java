package com.optipace.auth.service;

import com.optipace.auth.dto.AuthResponse;
import com.optipace.auth.dto.LoginRequest;
import com.optipace.auth.dto.RegisterRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;


public interface AuthService {

    AuthResponse register(@Valid RegisterRequest request);

    AuthResponse login(@Valid LoginRequest request);

    AuthResponse refreshToken(@NotBlank(message = "Refresh token is required") String refreshToken);
}
