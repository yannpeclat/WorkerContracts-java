package com.hrms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CorrelationIdFilter.
 * Valida geração e propagação do Correlation ID via MDC e headers.
 */
class CorrelationIdFilterTest {

    private CorrelationIdFilter correlationIdFilter;

    @BeforeEach
    void setUp() {
        correlationIdFilter = new CorrelationIdFilter();
    }

    @Test
    void shouldGenerateCorrelationIdWhenNotPresentInRequest() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String correlationId = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertNotNull(correlationId);
        assertFalse(correlationId.isBlank());
        // UUID tem formato específico: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
        assertTrue(correlationId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"),
                   "Correlation ID deve ser um UUID válido");
    }

    @Test
    void shouldPropagateCorrelationIdFromRequestHeader() throws Exception {
        // Arrange
        String existingCorrelationId = "test-correlation-id-12345";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, existingCorrelationId);
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String returnedCorrelationId = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertEquals(existingCorrelationId, returnedCorrelationId);
    }

    @Test
    void shouldClearMdcAfterFilterExecution() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilterInternal(request, response, filterChain);

        // Assert - MDC deve estar limpo após a execução do filtro
        // (isto é validado indiretamente pois o MDC é thread-local)
        assertNull(org.slf4j.MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY),
                   "MDC deve estar limpo após execução do filtro");
    }

    @Test
    void shouldHandleBlankCorrelationIdAsNotPresent() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, "   ");
        
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Act
        correlationIdFilter.doFilterInternal(request, response, filterChain);

        // Assert
        String correlationId = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertNotNull(correlationId);
        assertNotEquals("   ", correlationId);
        assertFalse(correlationId.isBlank());
    }
}
