package com.hrms.dto;

import com.hrms.domain.enums.ContractType;
import com.hrms.domain.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDTO {
    
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private ContractType type;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal salary;
    private String currency;
    private Integer weeklyHours;
    private ContractTermsDTO terms;
    private ContractStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
