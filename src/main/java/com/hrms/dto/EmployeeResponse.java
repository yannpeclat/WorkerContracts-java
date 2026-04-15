--- src/main/java/com/hrms/dto/EmployeeResponse.java (原始)


+++ src/main/java/com/hrms/dto/EmployeeResponse.java (修改后)
package com.hrms.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.hrms.enums.EmployeeStatus;

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
    private List<AddressResponse> addresses;
    private Instant createdAt;
    private Instant updatedAt;
}