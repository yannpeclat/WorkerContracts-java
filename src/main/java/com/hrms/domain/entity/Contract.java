package com.hrms.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.hrms.domain.enums.ContractType;
import com.hrms.domain.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private Employee employee;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Contract type is required")
    private ContractType type;
    
    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero")
    @Digits(integer = 12, fraction = 2, message = "Invalid salary format")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salary;
    
    @Size(max = 3, message = "Currency code must be at most 3 characters")
    @Column(length = 3)
    private String currency;
    
    @Min(value = 1, message = "Weekly hours must be at least 1")
    @Max(value = 80, message = "Weekly hours cannot exceed 80")
    @Column(name = "weekly_hours")
    private Integer weeklyHours;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "benefits", column = @Column(name = "terms_benefits")),
        @AttributeOverride(name = "bonusPolicy", column = @Column(name = "terms_bonus_policy")),
        @AttributeOverride(name = "vacationDays", column = @Column(name = "terms_vacation_days")),
        @AttributeOverride(name = "terminationPolicy", column = @Column(name = "terms_termination_policy"))
    })
    private ContractTerms terms;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;
    
    @Column(name = "updated_at")
    private LocalDate updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.status = ContractStatus.ACTIVE;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDate.now();
    }
}
