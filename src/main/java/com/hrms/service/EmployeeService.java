package com.hrms.service;

import com.hrms.domain.entity.Employee;
import com.hrms.domain.enums.EmployeeStatus;
import com.hrms.dto.AddressDTO;
import com.hrms.dto.EmployeeRequestDTO;
import com.hrms.dto.EmployeeResponseDTO;
import com.hrms.exception.BusinessException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.metrics.CustomMetrics;
import com.hrms.repository.EmployeeRepository;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class EmployeeService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private static final int MIN_AGE = 16;
    
    private final EmployeeRepository employeeRepository;
    private final CustomMetrics customMetrics;
    
    public EmployeeService(EmployeeRepository employeeRepository, CustomMetrics customMetrics) {
        this.employeeRepository = employeeRepository;
        this.customMetrics = customMetrics;
    }
    
    @Transactional(readOnly = true)
    @Timed(value = "hrms.operation.duration", description = "Time to find all employees")
    public Page<EmployeeResponseDTO> findAll(Pageable pageable) {
        logger.debug("Finding all employees with pagination: {}", pageable);
        return employeeRepository.findAll(pageable).map(this::toResponseDTO);
    }
    
    @Transactional(readOnly = true)
    @Timed(value = "hrms.operation.duration", description = "Time to find employee by ID")
    public EmployeeResponseDTO findById(UUID id) {
        logger.debug("Finding employee by id: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return toResponseDTO(employee);
    }
    
    /**
     * Busca funcionário por CPF com retry para operações concorrentes.
     * Aplica retry com backoff exponencial em caso de falha temporária.
     */
    @Transactional(readOnly = true)
    @Retryable(
        value = {org.springframework.dao.CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    @Timed(value = "hrms.operation.duration", description = "Time to find employee by CPF")
    public EmployeeResponseDTO findByCpf(String cpf) {
        logger.debug("Finding employee by cpf: {}", cpf);
        Employee employee = employeeRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with CPF " + cpf));
        return toResponseDTO(employee);
    }
    
    /**
     * Cria novo funcionário com lock pessimista para evitar duplicação concorrente.
     */
    @Retryable(
        value = {org.springframework.dao.CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    @Timed(value = "hrms.operation.duration", description = "Time to create employee")
    public EmployeeResponseDTO create(EmployeeRequestDTO requestDTO) {
        logger.info("Creating new employee with CPF: {}", requestDTO.getCpf());
        
        validateAge(requestDTO.getBirthDate());
        
        // Usa lock pessimista para evitar race condition na verificação de CPF duplicado
        employeeRepository.findByCpfWithPessimisticLock(requestDTO.getCpf()).ifPresent(existing -> {
            throw new BusinessException("CPF already registered", "CPF_ALREADY_EXISTS");
        });
        
        if (employeeRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BusinessException("Email already registered", "EMAIL_ALREADY_EXISTS");
        }
        
        Employee employee = toEntity(requestDTO);
        Employee saved = employeeRepository.save(employee);
        logger.info("Employee created successfully with id: {}", saved.getId());
        
        // Atualiza métricas
        customMetrics.updateActiveEmployeesCount(countActiveEmployees());
        
        return toResponseDTO(saved);
    }
    
    /**
     * Atualiza funcionário com lock pessimista para consistência.
     */
    @Retryable(
        value = {org.springframework.dao.CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    @Timed(value = "hrms.operation.duration", description = "Time to update employee")
    public EmployeeResponseDTO update(UUID id, EmployeeRequestDTO requestDTO) {
        logger.info("Updating employee with id: {}", id);
        
        validateAge(requestDTO.getBirthDate());
        
        // Usa lock pessimista para evitar race condition
        Employee employee = employeeRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        
        employeeRepository.findByCpfWithPessimisticLock(requestDTO.getCpf()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("CPF already registered", "CPF_ALREADY_EXISTS");
            }
        });
        
        employeeRepository.findByEmail(requestDTO.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("Email already registered", "EMAIL_ALREADY_EXISTS");
            }
        });
        
        employee.setName(requestDTO.getName());
        employee.setCpf(requestDTO.getCpf());
        employee.setEmail(requestDTO.getEmail());
        employee.setPhone(requestDTO.getPhone());
        employee.setBirthDate(requestDTO.getBirthDate());
        employee.setAddress(toAddress(requestDTO.getAddress()));
        
        Employee updated = employeeRepository.save(employee);
        logger.info("Employee updated successfully with id: {}", updated.getId());
        return toResponseDTO(updated);
    }
    
    /**
     * Desativa funcionário com lock pessimista.
     */
    @Retryable(
        value = {org.springframework.dao.CannotAcquireLockException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
    )
    @Timed(value = "hrms.operation.duration", description = "Time to deactivate employee")
    public void deactivate(UUID id) {
        logger.info("Deactivating employee with id: {}", id);
        Employee employee = employeeRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
        
        // Atualiza métricas
        customMetrics.updateActiveEmployeesCount(countActiveEmployees());
        
        logger.info("Employee deactivated successfully with id: {}", id);
    }
    
    public void delete(UUID id) {
        logger.info("Deleting employee with id: {}", id);
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", id);
        }
        employeeRepository.deleteById(id);
        
        // Atualiza métricas
        customMetrics.updateActiveEmployeesCount(countActiveEmployees());
        
        logger.info("Employee deleted successfully with id: {}", id);
    }
    
    /**
     * Conta número de funcionários ativos para métricas.
     */
    @Transactional(readOnly = true)
    public long countActiveEmployees() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .count();
    }
    
    private void validateAge(LocalDate birthDate) {
        int age = LocalDate.now().getYear() - birthDate.getYear();
        if (age < MIN_AGE) {
            throw new BusinessException("Employee must be at least " + MIN_AGE + " years old", "INVALID_AGE");
        }
    }
    
    private Employee toEntity(EmployeeRequestDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setCpf(dto.getCpf());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setBirthDate(dto.getBirthDate());
        employee.setAddress(toAddress(dto.getAddress()));
        return employee;
    }
    
    private AddressDTO toAddressDTO(com.hrms.domain.entity.Address address) {
        if (address == null) return null;
        return new AddressDTO(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
        );
    }
    
    private com.hrms.domain.entity.Address toAddress(AddressDTO dto) {
        if (dto == null) return null;
        return new com.hrms.domain.entity.Address(
                dto.getStreet(),
                dto.getCity(),
                dto.getState(),
                dto.getZipCode(),
                dto.getCountry()
        );
    }
    
    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setCpf(employee.getCpf());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setBirthDate(employee.getBirthDate());
        dto.setAddress(toAddressDTO(employee.getAddress()));
        dto.setStatus(employee.getStatus());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        dto.setAge(employee.getAge());
        return dto;
    }
}
