package com.hrms.dto;

import com.hrms.domain.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    
    private UUID id;
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private AddressDTO address;
    private EmployeeStatus status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Integer age;
}
