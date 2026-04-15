package com.hrms.service;

import com.hrms.config.JwtTokenProvider;
import com.hrms.dto.auth.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Serviço para operações de autenticação.
 * 
 * Responsabilidades:
 * - Geração de tokens JWT
 * - Refresh de tokens
 * - Validação de tokens
 * 
 * @author HRMS Team
 */
@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final JwtTokenProvider tokenProvider;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    public AuthenticationService(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Gera tokens de acesso e refresh para um usuário autenticado.
     * 
     * @param userDetails detalhes do usuário
     * @return resposta com tokens
     */
    public AuthResponse generateTokens(UserDetails userDetails) {
        String username = userDetails.getUsername();
        
        // Cria claims adicionais (roles, etc.)
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        
        // Gera tokens
        String accessToken = tokenProvider.generateAccessToken(username, claims);
        String refreshToken = tokenProvider.generateRefreshToken(username);
        
        logger.debug("Generated tokens for user: {}", username);
        
        return new AuthResponse(
            accessToken,
            refreshToken,
            username,
            jwtExpiration / 1000 // Retorna em segundos
        );
    }

    /**
     * Renova o token de acesso usando um refresh token válido.
     * 
     * @param refreshToken token de refresh
     * @return novos tokens
     */
    public AuthResponse refreshAccessToken(String refreshToken) {
        // Valida o refresh token
        if (!tokenProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        
        String username = tokenProvider.getUsernameFromToken(refreshToken);
        
        // Gera novo access token
        Map<String, Object> claims = new HashMap<>();
        String newAccessToken = tokenProvider.generateAccessToken(username, claims);
        
        // Opcional: gerar novo refresh token também (refresh token rotation)
        String newRefreshToken = tokenProvider.generateRefreshToken(username);
        
        logger.debug("Refreshed tokens for user: {}", username);
        
        return new AuthResponse(
            newAccessToken,
            newRefreshToken,
            username,
            jwtExpiration / 1000
        );
    }
}
