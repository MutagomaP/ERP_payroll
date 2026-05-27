package com.rwanda.gov.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeductionRateRequest {
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Deduction name is required")
    private String deductionName;
    
    @NotNull(message = "Percentage is required")
    private BigDecimal percentage;
}
