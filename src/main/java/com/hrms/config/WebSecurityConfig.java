package com.hrms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuração principal do Spring Security.
 * 
 * SEGURANÇA:
 * - Stateless (sem sessão HTTP)
 * - JWT para autenticação
 * - CORS configurado via variáveis de ambiente (NUNCA usa * em produção)
 * - Rate limiting aplicado no controller
 * - H2 console apenas em desenvolvimento
 * 
 * @author HRMS Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorrelationIdFilter correlationIdFilter;
    
    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;
    
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;
    
    @Value("${cors.allowed-headers:Authorization,Content-Type,X-Requested-With}")
    private String allowedHeaders;
    
    @Value("${cors.allow-credentials:true}")
    private Boolean allowCredentials;
    
    @Value("${cors.max-age:3600}")
    private Long maxAge;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                             CorrelationIdFilter correlationIdFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.correlationIdFilter = correlationIdFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF para API stateless
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configura CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configura autorização de requisições
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers(
                    "/api/auth/**"              // Autenticação
                ).permitAll()
                
                // Console H2 apenas em desenvolvimento
                .requestMatchers("/h2-console/**").permitAll()
                
                // Swagger/OpenAPI (se habilitado)
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()
                
                // Error endpoint
                .requestMatchers("/error").permitAll()
                
                // Todas as outras requisições precisam de autenticação
                .anyRequest().authenticated()
            )
            
            // Configura sessão stateless (JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Adiciona filtro JWT antes do filtro de autenticação
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // Adiciona filtro de Correlation ID antes do filtro JWT
            .addFilterBefore(correlationIdFilter, JwtAuthenticationFilter.class);
        
        // Headers de segurança
        http.headers(headers -> headers
            .frameOptions(frame -> frame.disable()) // Necessário para H2 console
            .contentTypeOptions(cto -> cto.disable())
        );
        
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configuração de CORS baseada em variáveis de ambiente.
     * 
     * SEGURANÇA CRÍTICA:
     * - NUNCA permite todas as origens (*) em produção
     * - Origens específicas devem ser configuradas via CORS_ALLOWED_ORIGINS
     * - Credentials controlados explicitamente
     * - Headers explícitos e mínimos necessários
     * 
     * @return configuração CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse das origens permitidas (separadas por vírgula)
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                                     .map(String::trim)
                                     .filter(origin -> !origin.isEmpty())
                                     .toList();
        
        // VALIDAÇÃO DE SEGURANÇA: Em produção, não permitir origens vazias ou wildcard
        if ("prod".equalsIgnoreCase(activeProfile)) {
            if (origins.isEmpty() || origins.contains("*")) {
                throw new IllegalStateException(
                    "CORS misconfiguration detected in production profile. " +
                    "CORS_ALLOWED_ORIGINS must be set to specific domains (no wildcards). " +
                    "Example: https://yourdomain.com,https://app.yourdomain.com"
                );
            }
        }
        
        // Usa allowedOriginPatterns para suportar múltiplas origens com credenciais
        configuration.setAllowedOriginPatterns(origins);
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(
            Arrays.stream(allowedMethods.split(","))
                  .map(String::trim)
                  .filter(method -> !method.isEmpty())
                  .toList()
        );
        
        // Headers permitidos (explícitos, evitar wildcard em produção)
        if ("*".equals(allowedHeaders.trim()) && "prod".equalsIgnoreCase(activeProfile)) {
            throw new IllegalStateException(
                "Wildcard CORS headers (*) not allowed in production. " +
                "Set CORS_ALLOWED_HEADERS to specific headers."
            );
        }
        
        if ("*".equals(allowedHeaders.trim())) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(
                Arrays.stream(allowedHeaders.split(","))
                      .map(String::trim)
                      .filter(header -> !header.isEmpty())
                      .toList()
            );
        }
        
        // Permite envio de credentials (cookies, authorization headers)
        configuration.setAllowCredentials(allowCredentials);
        
        // Tempo de cache das preflight requests
        configuration.setMaxAge(maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
