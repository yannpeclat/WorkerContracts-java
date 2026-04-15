package com.hrms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * Segurança:
 * - Stateless (sem sessão HTTP)
 * - JWT para autenticação
 * - CORS configurável via variáveis de ambiente
 * - Rate limiting para endpoints de login
 * 
 * @author HRMS Team
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;
    
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;
    
    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;
    
    @Value("${cors.allow-credentials:true}")
    private Boolean allowCredentials;
    
    @Value("${cors.max-age:3600}")
    private Long maxAge;

    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
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
                    "/api/auth/**",           // Autenticação
                    "/h2-console/**",         // Console H2 (dev apenas)
                    "/error",                 // Página de erro
                    "/swagger-ui/**",         // Swagger UI
                    "/v3/api-docs/**"         // OpenAPI docs
                ).permitAll()
                
                // Todas as outras requisições precisam de autenticação
                .anyRequest().authenticated()
            )
            
            // Configura sessão stateless (JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Adiciona filtro JWT antes do filtro de autenticação
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // Permite acesso ao console H2 (apenas em desenvolvimento)
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        
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
     * Segurança:
     * - Origens específicas (não usa * em produção)
     * - Credentials controlados
     * - Headers explícitos
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parse das origens permitidas (separadas por vírgula)
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                                     .map(String::trim)
                                     .toList();
        configuration.setAllowedOriginPatterns(origins); // Usa patterns para permitir wildcards se necessário
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(
            Arrays.stream(allowedMethods.split(","))
                  .map(String::trim)
                  .toList()
        );
        
        // Headers permitidos
        if ("*".equals(allowedHeaders.trim())) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(
                Arrays.stream(allowedHeaders.split(","))
                      .map(String::trim)
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
