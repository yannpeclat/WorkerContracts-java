package com.hrms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractTermsDTO {
    
    @Size(max = 500, message = "Benefits description must be at most 500 characters")
    private String benefits;
    
    @Size(max = 200, message = "Bonus policy must be at most 200 characters")
    private String bonusPolicy;
    
    @Min(value = 0, message = "Vacation days must be non-negative")
    private Integer vacationDays;
    
    @Size(max = 300, message = "Termination policy must be at most 300 characters")
    private String terminationPolicy;
}
