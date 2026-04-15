package com.hrms.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequestDTO {
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    private String name;
    
    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "^\\d{11}$", message = "CPF must contain exactly 11 digits")
    private String cpf;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must be a valid international format")
    private String phone;
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    @NotNull(message = "Address is required")
    private AddressDTO address;
}
