package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    
    @NotBlank(message = "Street is required")
    @Size(max = 200, message = "Street must be at most 200 characters")
    private String street;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;
    
    @NotBlank(message = "State is required")
    @Size(max = 50, message = "State must be at most 50 characters")
    private String state;
    
    @NotBlank(message = "ZipCode is required")
    @Size(max = 20, message = "ZipCode must be at most 20 characters")
    private String zipCode;
    
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must be at most 100 characters")
    private String country;
}
