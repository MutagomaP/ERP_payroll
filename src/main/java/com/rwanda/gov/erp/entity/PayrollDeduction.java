package com.rwanda.gov.erp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_deduction", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"employment_id", "month", "year"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollDeduction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employment_id", nullable = false)
    private Employment employment;
    
    @NotNull
    @Column(name = "base_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;
    
    @Column(name = "housing_allowance", precision = 15, scale = 2)
    private BigDecimal housingAllowance;
    
    @Column(name = "transport_allowance", precision = 15, scale = 2)
    private BigDecimal transportAllowance;
    
    @Column(name = "gross_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossSalary;
    
    @Column(name = "employee_tax_amount", precision = 15, scale = 2)
    private BigDecimal employeeTaxAmount;
    
    @Column(name = "pension_amount", precision = 15, scale = 2)
    private BigDecimal pensionAmount;
    
    @Column(name = "medical_insurance_amount", precision = 15, scale = 2)
    private BigDecimal medicalInsuranceAmount;
    
    @Column(name = "other_deduction_amount", precision = 15, scale = 2)
    private BigDecimal otherDeductionAmount;
    
    @Column(name = "net_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal netSalary;
    
    @NotNull
    @Column(nullable = false)
    private Integer month;
    
    @NotNull
    @Column(nullable = false)
    private Integer year;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status = PayrollStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum PayrollStatus {
        PENDING, PAID
    }
}
