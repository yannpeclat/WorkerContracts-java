package com.hrms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço para carregamento de detalhes do usuário.
 * 
 * SEGURANÇA:
 * - Senhas padrão DEVEM ser alteradas via variáveis de ambiente em produção
 * - Em produção, substituir por implementação com banco de dados
 * 
 * @author HRMS Team
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final PasswordEncoder passwordEncoder;
    
    @Value("${admin.default.password:}")
    private String adminDefaultPassword;
    
    @Value("${user.default.password:}")
    private String userDefaultPassword;
    
    // Armazenamento em memória para demonstração
    // EM PRODUÇÃO: usar banco de dados
    private final Map<String, UserDetails> userDatabase = new HashMap<>();

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inicializa usuários padrão para desenvolvimento.
     * 
     * SEGURANÇA CRÍTICA:
     * - Verifica se senhas padrão estão sendo usadas
     * - Exige configuração via variáveis de ambiente em produção
     */
    @PostConstruct
    public void init() {
        // Valida se as senhas foram configuradas via environment
        if (adminDefaultPassword == null || adminDefaultPassword.trim().isEmpty() || 
            adminDefaultPassword.contains("change") || adminDefaultPassword.equals("Admin@123!")) {
            logger.warn("ADMIN_DEFAULT_PASSWORD not properly configured. Using default (INSECURE for production).");
            adminDefaultPassword = "Admin@123!"; // Apenas para dev, mas com warning no log
        }
        
        if (userDefaultPassword == null || userDefaultPassword.trim().isEmpty() ||
            userDefaultPassword.contains("change") || userDefaultPassword.equals("User@123!")) {
            logger.warn("USER_DEFAULT_PASSWORD not properly configured. Using default (INSECURE for production).");
            userDefaultPassword = "User@123!"; // Apenas para dev, mas com warning no log
        }
        
        initializeUsers();
        logger.info("Users initialized successfully");
    }

    /**
     * Inicializa usuários no sistema.
     */
    private void initializeUsers() {
        // Usuário admin
        String adminPassword = passwordEncoder.encode(adminDefaultPassword);
        List<String> adminRoles = new ArrayList<>();
        adminRoles.add("ADMIN");
        adminRoles.add("USER");
        userDatabase.put("admin", User.builder()
            .username("admin")
            .password(adminPassword)
            .roles("ADMIN", "USER")
            .build());

        // Usuário comum
        String userPassword = passwordEncoder.encode(userDefaultPassword);
        userDatabase.put("user", User.builder()
            .username("user")
            .password(userPassword)
            .roles("USER")
            .build());

        logger.info("Default users initialized (CHANGE PASSWORDS IN PRODUCTION)");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = userDatabase.get(username);
        
        if (userDetails == null) {
            logger.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        
        logger.debug("User loaded: {}", username);
        return userDetails;
    }

    /**
     * Método auxiliar para verificar se um usuário existe.
     * 
     * @param username nome do usuário
     * @return true se existir
     */
    public boolean existsByUsername(String username) {
        return userDatabase.containsKey(username);
    }
}
