--- src/main/java/com/hrms/repository/UserRepository.java (原始)


+++ src/main/java/com/hrms/repository/UserRepository.java (修改后)
package com.hrms.repository;

import com.hrms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}