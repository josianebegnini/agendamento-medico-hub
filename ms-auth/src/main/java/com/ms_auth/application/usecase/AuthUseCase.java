package com.ms_auth.application.usecase;

import com.msauth.application.dto.AuthResponse;
import com.msauth.application.dto.LoginRequest;
import com.msauth.application.dto.UserCreateRequest;
import com.msauth.domain.entity.User;
import com.msauth.domain.service.TokenService;
import com.msauth.domain.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthUseCase {
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthUseCase(UserService userService, TokenService tokenService,
                       PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.validateUser(request.username(), request.password());
        String token = tokenService.generateToken(user.getUsername());

        return new AuthResponse(
                token,
                "Bearer",
                3600L,
                user.getUsername()
        );
    }

    public AuthResponse register(UserCreateRequest request) {
        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                List.of(Role.ROLE_USER)
        );

        User savedUser = userService.createUser(user);
        String token = tokenService.generateToken(savedUser.getUsername());

        return new AuthResponse(
                token,
                "Bearer",
                3600L,
                savedUser.getUsername()
        );
    }
}