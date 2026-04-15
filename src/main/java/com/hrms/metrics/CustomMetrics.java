package com.hrms.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Métricas customizadas para o HR Management System.
 * 
 * Expõe métricas importantes para monitoramento via Prometheus:
 * - Contador de funcionários ativos
 * - Tempo médio de processamento de requisições
 * - Taxa de erros por tipo
 * - Status do banco de dados
 */
@Component
public class CustomMetrics {

    private final MeterRegistry meterRegistry;
    private final DataSource dataSource;
    
    // Contadores
    private final AtomicLong activeEmployeesCount = new AtomicLong(0);
    private final AtomicLong failedLoginAttempts = new AtomicLong(0);
    private final AtomicLong successfulLogins = new AtomicLong(0);

    public CustomMetrics(MeterRegistry meterRegistry, DataSource dataSource) {
        this.meterRegistry = meterRegistry;
        this.dataSource = dataSource;
        
        registerMetrics();
    }

    private void registerMetrics() {
        // Gauge para contagem de funcionários ativos
        Gauge.builder("hrms.employees.active.count", activeEmployeesCount, AtomicLong::get)
             .description("Número de funcionários ativos no sistema")
             .tag("application", "hr-management-system")
             .register(meterRegistry);

        // Counter para tentativas de login falhas
        Counter.builder("hrms.auth.login.failed.total")
               .description("Total de tentativas de login falhas")
               .tag("application", "hr-management-system")
               .register(meterRegistry);

        // Counter para logins bem-sucedidos
        Counter.builder("hrms.auth.login.success.total")
               .description("Total de logins bem-sucedidos")
               .tag("application", "hr-management-system")
               .register(meterRegistry);

        // Timer para tempo de processamento de operações críticas
        Timer.builder("hrms.operation.duration")
             .description("Tempo de duração das operações do sistema")
             .tag("application", "hr-management-system")
             .publishPercentiles(0.5, 0.95, 0.99)
             .publishPercentileHistogram()
             .register(meterRegistry);

        // Gauge para status da conexão com banco de dados
        Gauge.builder("hrms.database.connection.status", this, (m) -> m.getDatabaseConnectionStatus())
             .description("Status da conexão com o banco de dados (1=saudável, 0=inativo)")
             .tag("application", "hr-management-system")
             .register(meterRegistry);
    }

    /**
     * Incrementa o contador de tentativas de login falhas.
     */
    public void incrementFailedLogin() {
        failedLoginAttempts.incrementAndGet();
        meterRegistry.counter("hrms.auth.login.failed.total").increment();
    }

    /**
     * Incrementa o contador de logins bem-sucedidos.
     */
    public void incrementSuccessfulLogin() {
        successfulLogins.incrementAndGet();
        meterRegistry.counter("hrms.auth.login.success.total").increment();
    }

    /**
     * Atualiza a contagem de funcionários ativos.
     */
    public void updateActiveEmployeesCount(long count) {
        activeEmployeesCount.set(count);
    }

    /**
     * Verifica se a conexão com o banco de dados está saudável.
     * Retorna 1 se saudável, 0 caso contrário.
     */
    public int getDatabaseConnectionStatus() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return 1;
            }
        } catch (SQLException e) {
            return 0;
        }
        return 0;
    }
}
