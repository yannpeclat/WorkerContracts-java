package com.hrms.dto;

import com.hrms.enums.EmployeeStatus;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private EmployeeStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
