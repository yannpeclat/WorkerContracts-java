package com.hrms.config.retry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Configuração de Retry com backoff exponencial.
 * 
 * O retry é aplicado em operações que podem falhar temporariamente:
 * - Chamadas a banco de dados (deadlocks, timeouts)
 * - Chamadas a serviços externos
 * - Operações de I/O
 * 
 * Backoff Exponencial:
 * - Initial interval: 1 segundo
 * - Multiplier: 2.0 (dobra a cada tentativa)
 * - Max interval: 10 segundos
 * - Max attempts: 3
 */
@Configuration
@EnableRetry(proxyTargetClass = true)
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        // Política de retry simples (max 3 tentativas)
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        // Política de backoff exponencial
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000L); // 1 segundo
        backOffPolicy.setMultiplier(2.0);        // Dobra a cada tentativa
        backOffPolicy.setMaxInterval(10000L);    // Máximo de 10 segundos
        retryTemplate.setBackOffPolicy(backOffPolicy);
        
        return retryTemplate;
    }
}
