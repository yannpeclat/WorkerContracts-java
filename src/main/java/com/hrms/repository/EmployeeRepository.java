package com.hrms.repository;

import com.hrms.domain.entity.Employee;
import com.hrms.domain.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    
    Optional<Employee> findByCpf(String cpf);
    
    Optional<Employee> findByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);
    
    Page<Employee> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE " +
           "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(e.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Employee> findEmployees(
        @Param("name") String name,
        @Param("email") String email,
        @Param("status") EmployeeStatus status,
        Pageable pageable
    );
}
