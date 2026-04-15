package com.hrms.repository;

import com.hrms.domain.entity.Contract;
import com.hrms.domain.entity.Employee;
import com.hrms.domain.enums.ContractStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContractRepository extends JpaRepository<Contract, UUID> {
    
    List<Contract> findByEmployee(Employee employee);
    
    Page<Contract> findByEmployee(Employee employee, Pageable pageable);
    
    Optional<Contract> findByEmployeeAndStatus(Employee employee, ContractStatus status);
    
    @Query("SELECT c FROM Contract c WHERE c.employee = :employee AND c.status = :status")
    Optional<Contract> findActiveContractByEmployee(@Param("employee") Employee employee, 
                                                     @Param("status") ContractStatus status);
    
    boolean existsByEmployeeAndStatus(Employee employee, ContractStatus status);
    
    Page<Contract> findByStatus(ContractStatus status, Pageable pageable);
    
    @Query("SELECT c FROM Contract c WHERE " +
           "(:employeeId IS NULL OR c.employee.id = :employeeId) AND " +
           "(:status IS NULL OR c.status = :status)")
    Page<Contract> findContracts(
        @Param("employeeId") UUID employeeId,
        @Param("status") ContractStatus status,
        Pageable pageable
    );
}
