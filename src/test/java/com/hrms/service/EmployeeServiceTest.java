package com.hrms.service;

import com.hrms.domain.entity.Employee;
import com.hrms.domain.enums.EmployeeStatus;
import com.hrms.dto.EmployeeRequestDTO;
import com.hrms.dto.EmployeeResponseDTO;
import com.hrms.exception.BusinessException;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.metrics.CustomMetrics;
import com.hrms.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.CannotAcquireLockException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para EmployeeService.
 * Valida operações CRUD, validações e comportamento com locks pessimistas.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CustomMetrics customMetrics;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeRequestDTO requestDTO;
    private Employee employee;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();
        
        requestDTO = new EmployeeRequestDTO();
        requestDTO.setName("John Doe");
        requestDTO.setCpf("12345678901");
        requestDTO.setEmail("john.doe@example.com");
        requestDTO.setPhone("+5511999999999");
        requestDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        
        employee = new Employee();
        employee.setId(employeeId);
        employee.setName("John Doe");
        employee.setCpf("12345678901");
        employee.setEmail("john.doe@example.com");
        employee.setPhone("+5511999999999");
        employee.setBirthDate(LocalDate.of(1990, 1, 1));
        employee.setStatus(EmployeeStatus.ACTIVE);
    }

    @Test
    void shouldCreateEmployeeSuccessfully() {
        // Arrange
        when(employeeRepository.findByCpfWithPessimisticLock(anyString())).thenReturn(Optional.empty());
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeRepository.findAll()).thenReturn(java.util.List.of(employee));

        // Act
        EmployeeResponseDTO response = employeeService.create(requestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("12345678901", response.getCpf());
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(customMetrics, times(1)).updateActiveEmployeesCount(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        // Arrange
        when(employeeRepository.findByCpfWithPessimisticLock(anyString()))
                .thenReturn(Optional.of(new Employee()));

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> employeeService.create(requestDTO)
        );
        assertEquals("CPF_ALREADY_EXISTS", exception.getErrorCode());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(employeeRepository.findByCpfWithPessimisticLock(anyString())).thenReturn(Optional.empty());
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> employeeService.create(requestDTO)
        );
        assertEquals("EMAIL_ALREADY_EXISTS", exception.getErrorCode());
    }

    @Test
    void shouldFindEmployeeById() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        EmployeeResponseDTO response = employeeService.findById(employeeId);

        // Assert
        assertNotNull(response);
        assertEquals(employeeId, response.getId());
        assertEquals("John Doe", response.getName());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            ResourceNotFoundException.class,
            () -> employeeService.findById(employeeId)
        );
    }

    @Test
    void shouldDeactivateEmployee() {
        // Arrange
        when(employeeRepository.findByIdWithPessimisticLock(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeRepository.findAll()).thenReturn(java.util.List.of(employee));

        // Act
        employeeService.deactivate(employeeId);

        // Assert
        assertEquals(EmployeeStatus.INACTIVE, employee.getStatus());
        verify(employeeRepository, times(1)).save(employee);
        verify(customMetrics, times(1)).updateActiveEmployeesCount(anyLong());
    }

    @Test
    void shouldRetryOnLockException() {
        // Arrange
        when(employeeRepository.findByCpfWithPessimisticLock(anyString()))
                .thenThrow(new CannotAcquireLockException("Lock acquisition failed"))
                .thenThrow(new CannotAcquireLockException("Lock acquisition failed"))
                .thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeRepository.findAll()).thenReturn(java.util.List.of(employee));

        // Act
        EmployeeResponseDTO response = employeeService.create(requestDTO);

        // Assert
        assertNotNull(response);
        verify(employeeRepository, times(3)).findByCpfWithPessimisticLock(anyString());
    }

    @Test
    void shouldValidateMinimumAge() {
        // Arrange
        EmployeeRequestDTO underageRequest = new EmployeeRequestDTO();
        underageRequest.setName("Young Person");
        underageRequest.setCpf("98765432100");
        underageRequest.setEmail("young@example.com");
        underageRequest.setPhone("+5511999999998");
        underageRequest.setBirthDate(LocalDate.now().minusYears(15)); // 15 years old

        // Act & Assert
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> employeeService.create(underageRequest)
        );
        assertEquals("INVALID_AGE", exception.getErrorCode());
    }
}
