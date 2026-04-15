--- src/main/java/com/hrms/dto/LoginResponse.java (原始)


+++ src/main/java/com/hrms/dto/LoginResponse.java (修改后)
package com.hrms.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private UserInfo user;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfo {
        private java.util.UUID id;
        private String username;
        private String email;
        private String role;
    }
}