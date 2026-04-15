package com.hrms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação de Rate Limiting para prevenir ataques de força bruta.
 * 
 * Funcionamento:
 * - Limita número de tentativas de login por IP/username
 * - Janela deslizante de tempo configurável
 * - Thread-safe usando ConcurrentHashMap
 * 
 * @author HRMS Team
 */
@Component
public class RateLimiter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimiter.class);

    @Value("${login.rate-limit.max-attempts:5}")
    private int maxAttempts;

    @Value("${login.rate-limit.window-seconds:300}")
    private long windowSeconds;

    // Armazena tentativas falhas: key -> (timestamp, count)
    private final Map<String, AttemptInfo> attemptCache = new ConcurrentHashMap<>();

    /**
     * Registra uma tentativa falha de login.
     * 
     * @param key identificador (IP ou username)
     */
    public void recordFailedAttempt(String key) {
        Instant now = Instant.now();
        
        attemptCache.compute(key, (k, existing) -> {
            if (existing == null || isWindowExpired(existing)) {
                return new AttemptInfo(now, 1);
            }
            return new AttemptInfo(existing.windowStart, existing.count + 1);
        });

        logger.warn("Failed login attempt recorded for {}. Total attempts in window: {}", 
                    key, getAttemptCount(key));
    }

    /**
     * Verifica se o limite de tentativas foi excedido.
     * 
     * @param key identificador (IP ou username)
     * @return true se limite excedido
     */
    public boolean isLimitExceeded(String key) {
        AttemptInfo info = attemptCache.get(key);
        if (info == null) {
            return false;
        }

        if (isWindowExpired(info)) {
            attemptCache.remove(key);
            return false;
        }

        boolean exceeded = info.count >= maxAttempts;
        if (exceeded) {
            logger.warn("Rate limit exceeded for {}. Attempts: {}/{}", 
                       key, info.count, maxAttempts);
        }
        return exceeded;
    }

    /**
     * Reseta as tentativas para um identificador (após login bem-sucedido).
     * 
     * @param key identificador
     */
    public void resetAttempts(String key) {
        attemptCache.remove(key);
        logger.debug("Rate limit reset for {}", key);
    }

    /**
     * Obtém o número atual de tentativas.
     * 
     * @param key identificador
     * @return número de tentativas
     */
    public int getAttemptCount(String key) {
        AttemptInfo info = attemptCache.get(key);
        if (info == null || isWindowExpired(info)) {
            return 0;
        }
        return info.count;
    }

    /**
     * Limpa entradas expiradas do cache (pode ser chamado periodicamente).
     */
    public void cleanupExpiredEntries() {
        attemptCache.entrySet().removeIf(entry -> isWindowExpired(entry.getValue()));
        logger.debug("Cleaned up expired rate limit entries");
    }

    private boolean isWindowExpired(AttemptInfo info) {
        return Instant.now().isAfter(info.windowStart.plusSeconds(windowSeconds));
    }

    /**
     * Classe interna para armazenar informações das tentativas.
     */
    private static class AttemptInfo {
        final Instant windowStart;
        final int count;

        AttemptInfo(Instant windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
