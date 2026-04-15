package com.hrms.service;

import com.hrms.dto.EmployeeRequest;
import com.hrms.dto.EmployeeResponse;
import com.hrms.entity.Employee;
import com.hrms.enums.EmployeeStatus;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.EmployeeRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Timed(value = "employee.create", description = "Time to create an employee")
    @Retryable(
        retryFor = {org.springframework.dao.OptimisticLockingFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public EmployeeResponse create(EmployeeRequest request) {
        log.info("Creating employee with email: {}", request.getEmail());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        if (employeeRepository.existsByCpf(request.getCpf())) {
            throw new IllegalArgumentException("CPF already exists: " + request.getCpf());
        }

        Employee employee = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created with ID: {}", saved.getId());

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    @Timed(value = "employee.get", description = "Time to get an employee")
    public EmployeeResponse getById(UUID id) {
        log.debug("Getting employee by ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException("Employee not found with ID: " + id);
                });

        return toResponse(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAll() {
        log.debug("Getting all employees");
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Timed(value = "employee.update", description = "Time to update an employee")
    public EmployeeResponse update(UUID id, EmployeeRequest request) {
        log.info("Updating employee with ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employee.setName(request.getName());
        employee.setPhone(request.getPhone());

        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated with ID: {}", updated.getId());

        return toResponse(updated);
    }

    @Transactional
    @Timed(value = "employee.delete", description = "Time to delete an employee")
    public void delete(UUID id) {
        log.info("Soft deleting employee with ID: {}", id);

        Employee employee = employeeRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employee.softDelete();
        employeeRepository.save(employee);

        log.info("Employee soft deleted with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public long countByStatus(EmployeeStatus status) {
        return employeeRepository.countByStatus(status);
    }

    private EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .email(employee.getEmail())
                .cpf(employee.getCpf())
                .phone(employee.getPhone())
                .status(employee.getStatus())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}