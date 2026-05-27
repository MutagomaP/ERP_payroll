package com.rwanda.gov.erp.service;

import com.rwanda.gov.erp.dto.request.GeneratePayrollRequest;
import com.rwanda.gov.erp.entity.Employee;
import com.rwanda.gov.erp.entity.Employment;
import com.rwanda.gov.erp.entity.PayrollDeduction;
import com.rwanda.gov.erp.entity.PayrollMessage;
import com.rwanda.gov.erp.repository.EmploymentRepository;
import com.rwanda.gov.erp.repository.PayrollDeductionRepository;
import com.rwanda.gov.erp.repository.PayrollMessageRepository;
import com.rwanda.gov.erp.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollService {
    
    private final PayrollDeductionRepository payrollDeductionRepository;
    private final EmploymentRepository employmentRepository;
    private final PayrollMessageRepository payrollMessageRepository;
    
    // Deduction rates as per requirements
    private static final BigDecimal EMPLOYEE_TAX_RATE = new BigDecimal("30");
    private static final BigDecimal PENSION_RATE = new BigDecimal("6");
    private static final BigDecimal MEDICAL_INSURANCE_RATE = new BigDecimal("5");
    private static final BigDecimal CASH_ADVANCE_RATE = new BigDecimal("5");
    private static final BigDecimal SINKING_FUND_RATE = new BigDecimal("1");
    private static final BigDecimal HOUSING_RATE = new BigDecimal("14");
    private static final BigDecimal TRANSPORT_RATE = new BigDecimal("14");
    private static final BigDecimal HUNDRED = new BigDecimal("100");
    
    @Transactional
    public List<PayrollDeduction> generatePayroll(GeneratePayrollRequest request) {
        List<Employment> activeEmployments = employmentRepository.findByStatus(Employment.EmploymentStatus.ACTIVE);
        
        if (activeEmployments.isEmpty()) {
            throw new RuntimeException("No active employments found");
        }
        
        List<PayrollDeduction> payrollList = new ArrayList<>();
        
        for (Employment employment : activeEmployments) {
            // Check if employee is active
            if (employment.getEmployee().getStatus() != Employee.EmployeeStatus.ACTIVE) {
                continue;
            }
            
            // Check for duplicate payroll
            if (payrollDeductionRepository.findByEmploymentIdAndMonthAndYear(
                    employment.getId(), request.getMonth(), request.getYear()).isPresent()) {
                throw new RuntimeException("Payroll already exists for employee: " + 
                        employment.getEmployee().getFirstName() + " " + 
                        employment.getEmployee().getLastName() + 
                        " for month " + request.getMonth() + "/" + request.getYear());
            }
            
            PayrollDeduction payroll = calculatePayroll(employment, request.getMonth(), request.getYear());
            payrollList.add(payrollDeductionRepository.save(payroll));
        }
        
        return payrollList;
    }
    
    private PayrollDeduction calculatePayroll(Employment employment, Integer month, Integer year) {
        BigDecimal baseSalary = employment.getBaseSalary();
        
        // Calculate allowances
        BigDecimal housingAllowance = baseSalary.multiply(HOUSING_RATE).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        BigDecimal transportAllowance = baseSalary.multiply(TRANSPORT_RATE).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        
        // Calculate gross salary
        BigDecimal grossSalary = baseSalary.add(housingAllowance).add(transportAllowance);
        
        // Calculate deductions
        BigDecimal employeeTax = baseSalary.multiply(EMPLOYEE_TAX_RATE).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        BigDecimal pension = baseSalary.multiply(PENSION_RATE).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        BigDecimal medicalInsurance = baseSalary.multiply(MEDICAL_INSURANCE_RATE).divide(HUNDRED, 2, RoundingMode.HALF_UP);
        
        // Other deductions (Cash Advance + Sinking Fund)
        BigDecimal otherDeductions = baseSalary.multiply(CASH_ADVANCE_RATE.add(SINKING_FUND_RATE))
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
        
        // Calculate total deductions
        BigDecimal totalDeductions = employeeTax.add(pension).add(medicalInsurance).add(otherDeductions);
        
        // Validate that deductions don't exceed gross salary
        if (totalDeductions.compareTo(grossSalary) > 0) {
            throw new RuntimeException("Total deductions exceed gross salary for employee: " + 
                    employment.getEmployee().getFirstName() + " " + 
                    employment.getEmployee().getLastName());
        }
        
        // Calculate net salary
        BigDecimal netSalary = grossSalary.subtract(totalDeductions);
        
        // Create payroll record
        PayrollDeduction payroll = new PayrollDeduction();
        payroll.setCode("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payroll.setEmployment(employment);
        payroll.setBaseSalary(baseSalary);
        payroll.setHousingAllowance(housingAllowance);
        payroll.setTransportAllowance(transportAllowance);
        payroll.setGrossSalary(grossSalary);
        payroll.setEmployeeTaxAmount(employeeTax);
        payroll.setPensionAmount(pension);
        payroll.setMedicalInsuranceAmount(medicalInsurance);
        payroll.setOtherDeductionAmount(otherDeductions);
        payroll.setNetSalary(netSalary);
        payroll.setMonth(month);
        payroll.setYear(year);
        payroll.setStatus(PayrollDeduction.PayrollStatus.PENDING);
        
        return payroll;
    }
    
    public List<PayrollDeduction> getAllPayrolls() {
        return payrollDeductionRepository.findAll();
    }
    
    public PayrollDeduction getPayrollById(Long id) {
        return payrollDeductionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payroll not found with id: " + id));
    }
    
    public List<PayrollDeduction> getPayrollByMonthAndYear(Integer month, Integer year) {
        return payrollDeductionRepository.findByMonthAndYear(month, year);
    }
    
    public List<PayrollDeduction> getMyPayslips() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return payrollDeductionRepository.findByEmployeeId(userDetails.getId());
    }
    
    @Transactional
    public PayrollDeduction approvePayroll(Long id) {
        PayrollDeduction payroll = getPayrollById(id);
        
        if (payroll.getStatus() == PayrollDeduction.PayrollStatus.PAID) {
            throw new RuntimeException("Payroll already approved");
        }
        
        payroll.setStatus(PayrollDeduction.PayrollStatus.PAID);
        PayrollDeduction savedPayroll = payrollDeductionRepository.save(payroll);
        
        // Generate message for employee
        generatePayrollMessage(savedPayroll);
        
        return savedPayroll;
    }
    
    private void generatePayrollMessage(PayrollDeduction payroll) {
        Employee employee = payroll.getEmployment().getEmployee();
        
        String messageContent = String.format(
                "Dear %s, Your salary of %d/%d from Rwanda Government RWF %s has been credited to your %s account successfully.",
                employee.getFirstName(),
                payroll.getMonth(),
                payroll.getYear(),
                payroll.getNetSalary().toString(),
                payroll.getEmployment().getCode()
        );
        
        PayrollMessage message = new PayrollMessage();
        message.setPayrollDeduction(payroll);
        message.setEmployeeEmail(employee.getEmail());
        message.setMessageContent(messageContent);
        message.setSentStatus(PayrollMessage.MessageStatus.PENDING);
        
        payrollMessageRepository.save(message);
    }
    
    public List<PayrollMessage> getAllMessages() {
        return payrollMessageRepository.findAll();
    }
    
    public List<PayrollMessage> getMessagesByEmployeeId(Long employeeId) {
        return payrollMessageRepository.findByEmployeeId(employeeId);
    }
}
