package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.hrms.enums.ContractStatus;
import com.hrms.enums.ContractType;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false, length = 50)
    @Builder.Default
    private ContractType contractType = ContractType.CLT;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal salary;

    @Column(length = 3)
    @Builder.Default
    private String currency = "BRL";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String terms;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ContractTerms> termsList = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
