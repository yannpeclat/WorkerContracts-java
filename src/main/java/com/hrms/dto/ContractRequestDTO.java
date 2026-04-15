package com.hrms.dto;

import jakarta.validation.constraints.*;
import com.hrms.domain.enums.ContractType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequestDTO {
    
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    @NotNull(message = "Contract type is required")
    private ContractType type;
    
    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero")
    @Digits(integer = 12, fraction = 2, message = "Invalid salary format")
    private BigDecimal salary;
    
    @Size(max = 3, message = "Currency code must be at most 3 characters")
    private String currency;
    
    @Min(value = 1, message = "Weekly hours must be at least 1")
    @Max(value = 80, message = "Weekly hours cannot exceed 80")
    private Integer weeklyHours;
    
    private ContractTermsDTO terms;
}
