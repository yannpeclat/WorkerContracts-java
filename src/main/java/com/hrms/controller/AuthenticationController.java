package com.hrms.controller;

import com.hrms.dto.LoginRequest;
import com.hrms.dto.LoginResponse;
import com.hrms.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "APIs for user authentication")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get tokens")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        LoginResponse response = LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(authentication.getName())
                .role(authentication.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .orElse("USER"))
                .expiresIn(86400000L)
                .build();

        log.info("User {} authenticated successfully", request.getUsername());

        return ResponseEntity.ok(response);
    }
}