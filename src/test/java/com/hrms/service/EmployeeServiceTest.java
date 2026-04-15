package com.hrms.service;

import com.hrms.dto.EmployeeRequest;
import com.hrms.dto.EmployeeResponse;
import com.hrms.entity.Employee;
import com.hrms.enums.EmployeeStatus;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeRequest request;
    private Employee employee;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();

        request = EmployeeRequest.builder()
                .name("John Doe")
                .email("john@example.com")
                .cpf("123.456.789-00")
                .phone("123456789")
                .password("securePassword123")
                .build();

        employee = Employee.builder()
                .id(employeeId)
                .name(request.getName())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .phone(request.getPhone())
                .passwordHash("encodedPassword")
                .status(EmployeeStatus.ACTIVE)
                .build();
    }

    @Test
    void create_ShouldReturnEmployeeResponse_WhenValidRequest() {
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(employeeRepository.existsByCpf(request.getCpf())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse response = employeeService.create(request);

        assertNotNull(response);
        assertEquals(employeeId, response.getId());
        assertEquals(request.getEmail(), response.getEmail());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void create_ShouldThrowException_WhenEmailExists() {
        when(employeeRepository.existsByEmail(request.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.create(request)
        );

        assertTrue(exception.getMessage().contains("Email already exists"));
    }

    @Test
    void getById_ShouldReturnEmployeeResponse_WhenEmployeeExists() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        EmployeeResponse response = employeeService.getById(employeeId);

        assertNotNull(response);
        assertEquals(employeeId, response.getId());
        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void getById_ShouldThrowException_WhenEmployeeNotFound() {
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getById(employeeId)
        );

        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void delete_ShouldSoftDeleteEmployee_WhenEmployeeExists() {
        when(employeeRepository.findByIdWithPessimisticLock(employeeId))
                .thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.delete(employeeId);

        assertEquals(EmployeeStatus.TERMINATED, employee.getStatus());
        assertNotNull(employee.getDeletedAt());
        verify(employeeRepository).save(employee);
    }
}