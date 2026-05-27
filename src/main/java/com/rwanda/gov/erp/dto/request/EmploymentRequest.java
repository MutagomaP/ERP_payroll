package com.rwanda.gov.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmploymentRequest {
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotBlank(message = "Department is required")
    private String department;
    
    @NotBlank(message = "Position is required")
    private String position;
    
    @NotNull(message = "Base salary is required")
    private BigDecimal baseSalary;
    
    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;
}
