package com.hrms.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String refreshToken;
    private String username;
    private String role;
    private long expiresIn;
}
