--- src/main/java/com/hrms/dto/LoginRequest.java (原始)


+++ src/main/java/com/hrms/dto/LoginRequest.java (修改后)
package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}