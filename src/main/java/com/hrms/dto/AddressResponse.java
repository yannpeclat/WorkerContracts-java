--- src/main/java/com/hrms/dto/AddressResponse.java (原始)


+++ src/main/java/com/hrms/dto/AddressResponse.java (修改后)
package com.hrms.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private UUID id;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private Instant createdAt;
}