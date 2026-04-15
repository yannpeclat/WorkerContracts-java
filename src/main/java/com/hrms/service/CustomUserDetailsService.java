package com.hrms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço para carregamento de detalhes do usuário.
 * 
 * Em produção, isso deve buscar usuários do banco de dados.
 * Para demonstração, usa usuários em memória.
 * 
 * @author HRMS Team
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final PasswordEncoder passwordEncoder;
    
    // Armazenamento em memória para demonstração
    // EM PRODUÇÃO: usar banco de dados
    private final Map<String, UserDetails> userDatabase = new HashMap<>();

    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        initializeDefaultUsers();
    }

    /**
     * Inicializa usuários padrão para desenvolvimento.
     * 
     * Senhas padrão (dev apenas):
     * - admin / Admin@123!
     * - user / User@123!
     */
    private void initializeDefaultUsers() {
        // Usuário admin
        String adminPassword = passwordEncoder.encode("Admin@123!");
        List<String> adminRoles = new ArrayList<>();
        adminRoles.add("ROLE_ADMIN");
        adminRoles.add("ROLE_USER");
        userDatabase.put("admin", User.builder()
            .username("admin")
            .password(adminPassword)
            .roles("ADMIN", "USER")
            .build());

        // Usuário comum
        String userPassword = passwordEncoder.encode("User@123!");
        userDatabase.put("user", User.builder()
            .username("user")
            .password(userPassword)
            .roles("USER")
            .build());

        logger.info("Default users initialized for development");
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
