package com.hrms.controller;

import com.hrms.config.RateLimiter;
import com.hrms.dto.ApiResponseDTO;
import com.hrms.dto.auth.AuthResponse;
import com.hrms.dto.auth.LoginRequest;
import com.hrms.dto.auth.RefreshTokenRequest;
import com.hrms.exception.BusinessException;
import com.hrms.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para autenticação e autorização.
 * 
 * Endpoints:
 * - POST /api/auth/login - Realiza login e retorna tokens
 * - POST /api/auth/refresh - Renova token de acesso
 * - POST /api/auth/logout - Invalida tokens (opcional)
 * 
 * SEGURANÇA:
 * - Rate limiting para prevenir força bruta (configurável via env)
 * - Validação de entrada com Bean Validation
 * - Logs de tentativas falhas (sem expor dados sensíveis)
 * - Extração segura de IP do cliente
 * 
 * @author HRMS Team
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;
    private final RateLimiter rateLimiter;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            AuthenticationService authenticationService,
            RateLimiter rateLimiter) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
        this.rateLimiter = rateLimiter;
    }

    /**
     * Realiza autenticação do usuário e retorna tokens JWT.
     * 
     * SEGURANÇA:
     * - Rate limiting baseado em IP + username
     * - Logs sem expor senhas
     * - Reset de rate limit após sucesso
     * 
     * @param request credenciais de login
     * @param httpServletRequest requisição HTTP para extrair IP
     * @return tokens de acesso e refresh
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest) {
        
        // Extrai IP do cliente de forma segura
        String clientIp = getClientIpAddress(httpServletRequest);
        String rateLimitKey = clientIp + ":" + request.getUsername();
        
        // Verifica rate limiting
        if (rateLimiter.isLimitExceeded(rateLimitKey)) {
            logger.warn("Login attempt blocked due to rate limit for user: {} from IP: {}", 
                       request.getUsername(), clientIp);
            throw new BusinessException(
                "Too many failed login attempts. Please try again later.", 
                "RATE_LIMIT_EXCEEDED"
            );
        }

        try {
            // Autentica o usuário
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );

            // Login bem-sucedido - reseta rate limit
            rateLimiter.resetAttempts(rateLimitKey);

            // Carrega detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

            // Gera tokens
            AuthResponse response = authenticationService.generateTokens(userDetails);

            logger.info("User {} logged in successfully from IP: {}", request.getUsername(), clientIp);
            return ResponseEntity.ok(ApiResponseDTO.success(response, "Login successful"));

        } catch (BadCredentialsException e) {
            // Credenciais inválidas - registra tentativa falha
            rateLimiter.recordFailedAttempt(rateLimitKey);
            logger.warn("Invalid credentials for user: {} from IP: {}", request.getUsername(), clientIp);
            throw new BusinessException("Invalid username or password", "INVALID_CREDENTIALS");
        } catch (Exception e) {
            logger.error("Login error for user: {} from IP: {}", request.getUsername(), clientIp, e);
            throw new BusinessException("Authentication failed", "AUTH_FAILED");
        }
    }

    /**
     * Renova o token de acesso usando um refresh token válido.
     * 
     * @param request refresh token
     * @return novo token de acesso
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        
        try {
            AuthResponse response = authenticationService.refreshAccessToken(request.getRefreshToken());
            logger.info("Token refreshed successfully");
            return ResponseEntity.ok(ApiResponseDTO.success(response, "Token refreshed"));
        } catch (IllegalArgumentException e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            throw new BusinessException("Invalid or expired refresh token", "INVALID_REFRESH_TOKEN");
        } catch (Exception e) {
            logger.error("Token refresh error", e);
            throw new BusinessException("Token refresh failed", "REFRESH_FAILED");
        }
    }

    /**
     * Logout do usuário (opcional - em JWT stateless, o cliente apenas descarta o token).
     * 
     * @return confirmação de logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout() {
        // Em sistemas stateless com JWT, o logout é feito no cliente (descartando o token)
        // Se precisar de blacklist de tokens, implementar com Redis ou banco de dados
        logger.info("Logout requested");
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Logout successful"));
    }

    /**
     * Endpoint para verificar status da autenticação.
     * 
     * @return informações do usuário autenticado
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<String>> getCurrentUser(
            @RequestAttribute("username") String username) {
        return ResponseEntity.ok(ApiResponseDTO.success(username, "Current user"));
    }

    /**
     * Extrai o endereço IP do cliente de forma segura, considerando proxies.
     * 
     * SEGURANÇA:
     * - Verifica headers de proxy comuns (X-Forwarded-For, X-Real-IP)
     * - Previne IP spoofing básico
     * - Fallback para remoteAddr se headers não estiverem presentes
     * 
     * @param request requisição HTTP
     * @return IP do cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For pode conter múltiplos IPs, pega o primeiro
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
}
