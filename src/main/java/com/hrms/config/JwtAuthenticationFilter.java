package com.hrms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Filtro para autenticação via JWT.
 * 
 * Responsabilidades:
 * - Extrai token do header Authorization
 * - Valida token usando JwtTokenProvider
 * - Configura contexto de segurança do Spring
 * 
 * @author HRMS Team
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                // Verifica se é um token de access (não refresh)
                if (!tokenProvider.isRefreshToken(jwt) && tokenProvider.validateToken(jwt, username)) {
                    // Extrai roles do token (se existirem)
                    List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromToken(jwt);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                        );
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("Set security context for user: {}", username);
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            // Não lança exceção para não bloquear requisições não autenticadas
            // O Spring Security decidirá se a requisição pode prosseguir
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token JWT do header Authorization.
     * 
     * @param request requisição HTTP
     * @return token JWT ou null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }

    /**
     * Extrai as autoridades/roles do token JWT.
     * 
     * @param token token JWT
     * @return lista de autoridades
     */
    private List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        try {
            // Tenta extrair claim "roles" ou "authorities" do token
            Object rolesObj = tokenProvider.getClaimFromToken(token, 
                claims -> claims.get("roles"));
            
            if (rolesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Map<String, String>> roles = (List<Map<String, String>>) rolesObj;
                for (Map<String, String> role : roles) {
                    String roleName = role.get("authority") != null ? 
                                     role.get("authority") : role.get("role");
                    if (StringUtils.hasText(roleName)) {
                        authorities.add(new SimpleGrantedAuthority(roleName));
                    }
                }
            } else if (rolesObj instanceof String) {
                String[] roles = ((String) rolesObj).split(",");
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority(role.trim()));
                }
            }
        } catch (Exception e) {
            logger.debug("No roles found in token or error extracting: {}", e.getMessage());
            // Token sem roles é válido, usuário terá permissões básicas
        }
        
        // Se não houver roles, adiciona uma role padrão
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return authorities;
    }
}
