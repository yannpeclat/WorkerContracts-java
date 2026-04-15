package com.hrms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Filtro para gerar e propagar Correlation ID em todas as requisições.
 * O Correlation ID é adicionado ao MDC (Mapped Diagnostic Context) para logs estruturados
 * e propagado nos headers de resposta.
 */
@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Tenta obter o Correlation ID do header da requisição (propagação entre serviços)
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        
        // Se não existir, gera um novo UUID
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Adiciona ao MDC para logs estruturados
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
        
        try {
            // Adiciona ao header da resposta para o cliente conhecer o correlation ID
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            
            // Executa a cadeia de filtros
            filterChain.doFilter(request, response);
        } finally {
            // Limpa o MDC após o processamento da requisição
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}
