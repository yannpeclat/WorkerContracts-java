package com.hrms.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.hrms.domain.enums.EmployeeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 200, message = "Name must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    private String name;
    
    @NotBlank(message = "CPF is required")
    @Pattern(regexp = "^\\d{11}$", message = "CPF must contain exactly 11 digits")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone must be a valid international format")
    @Column(length = 20)
    private String phone;
    
    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "address_street")),
        @AttributeOverride(name = "city", column = @Column(name = "address_city")),
        @AttributeOverride(name = "state", column = @Column(name = "address_state")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code")),
        @AttributeOverride(name = "country", column = @Column(name = "address_country"))
    })
    private Address address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.status = EmployeeStatus.ACTIVE;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
    
    public int getAge() {
        if (birthDate == null) return 0;
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}
