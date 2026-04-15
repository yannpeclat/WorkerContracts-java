package com.hrms.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Componente para geração e validação de tokens JWT.
 * 
 * Segurança:
 * - Usa HS256 com chave secreta de 256 bits
 * - Tokens expiram em 1 hora por padrão
 * - Refresh tokens expiram em 24 horas
 * 
 * @author HRMS Team
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:86400000}")
    private long refreshExpiration;

    @Value("${jwt.issuer:hrms-api}")
    private String jwtIssuer;

    @Value("${jwt.audience:hrms-client}")
    private String jwtAudience;

    /**
     * Gera a chave secreta a partir da configuração.
     * A chave deve ter pelo menos 256 bits para HS256.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gera um token de acesso para o usuário.
     * 
     * @param username nome do usuário
     * @param roles    lista de roles/papéis
     * @return token JWT
     */
    public String generateAccessToken(String username, Map<String, Object> claims) {
        Map<String, Object> allClaims = new HashMap<>(claims);
        allClaims.put("type", "access");
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(allClaims)
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setAudience(jwtAudience)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Gera um token de refresh para renovação de sessão.
     * 
     * @param username nome do usuário
     * @return token de refresh JWT
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("type", "refresh")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrai o username do token.
     * 
     * @param token token JWT
     * @return username
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extrai uma claim específica do token.
     * 
     * @param token          token JWT
     * @param claimsResolver função para extrair a claim
     * @param <T>            tipo da claim
     * @return valor da claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todas as claims do token.
     * 
     * @param token token JWT
     * @return claims do token
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica se o token está expirado.
     * 
     * @param token token JWT
     * @return true se expirado
     */
    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Extrai a data de expiração do token.
     * 
     * @param token token JWT
     * @return data de expiração
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Valida o token JWT.
     * 
     * @param token    token JWT
     * @param username username esperado
     * @return true se válido
     */
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = getUsernameFromToken(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtém o tipo do token (access ou refresh).
     * 
     * @param token token JWT
     * @return tipo do token
     */
    public String getTokenType(String token) {
        return getClaimFromToken(token, claims -> claims.get("type", String.class));
    }

    /**
     * Verifica se o token é um token de refresh.
     * 
     * @param token token JWT
     * @return true se for refresh token
     */
    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    /**
     * Valida especificamente um refresh token.
     * 
     * @param token refresh token
     * @return true se válido
     */
    public boolean validateRefreshToken(String token) {
        try {
            if (!isRefreshToken(token)) {
                logger.warn("Token is not a refresh token");
                return false;
            }
            String username = getUsernameFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            logger.warn("Invalid refresh token: {}", e.getMessage());
            return false;
        }
    }
}
