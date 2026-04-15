--- src/main/java/com/hrms/repository/EmployeeRepository.java (原始)


+++ src/main/java/com/hrms/repository/EmployeeRepository.java (修改后)
package com.hrms.repository;

import com.hrms.entity.Employee;
import com.hrms.enums.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<Employee> findByStatus(EmployeeStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Optional<Employee> findByIdWithPessimisticLock(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT e FROM Employee e WHERE e.email = :email")
    Optional<Employee> findByEmailWithPessimisticLock(@Param("email") String email);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.status = :status")
    long countByStatus(@Param("status") EmployeeStatus status);
}