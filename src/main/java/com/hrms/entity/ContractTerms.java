--- src/main/java/com/hrms/entity/ContractTerms.java (原始)


+++ src/main/java/com/hrms/entity/ContractTerms.java (修改后)
package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "contract_terms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractTerms {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(name = "term_type", nullable = false, length = 50)
    private String termType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}