package com.rwanda.gov.erp.controller;

import com.rwanda.gov.erp.dto.request.GeneratePayrollRequest;
import com.rwanda.gov.erp.dto.response.ApiResponse;
import com.rwanda.gov.erp.entity.PayrollDeduction;
import com.rwanda.gov.erp.entity.PayrollMessage;
import com.rwanda.gov.erp.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payroll Management", description = "APIs for payroll generation and management")
public class PayrollController {
    
    private final PayrollService payrollService;
    
    @PostMapping("/generate")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Generate payroll", description = "Generate payroll for all active employees for a given month/year (MANAGER only)")
    public ResponseEntity<ApiResponse<List<PayrollDeduction>>> generatePayroll(@Valid @RequestBody GeneratePayrollRequest request) {
        try {
            List<PayrollDeduction> payrolls = payrollService.generatePayroll(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Payroll generated successfully for " + payrolls.size() + " employees", payrolls));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all payrolls", description = "Retrieve all payroll records (ADMIN, MANAGER only)")
    public ResponseEntity<ApiResponse<List<PayrollDeduction>>> getAllPayrolls() {
        List<PayrollDeduction> payrolls = payrollService.getAllPayrolls();
        return ResponseEntity.ok(ApiResponse.success("Payrolls retrieved successfully", payrolls));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get payroll by ID", description = "Retrieve payroll details by ID")
    public ResponseEntity<ApiResponse<PayrollDeduction>> getPayrollById(@PathVariable Long id) {
        try {
            PayrollDeduction payroll = payrollService.getPayrollById(id);
            return ResponseEntity.ok(ApiResponse.success("Payroll retrieved successfully", payroll));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get payroll by month and year", description = "Retrieve all payrolls for a specific month/year")
    public ResponseEntity<ApiResponse<List<PayrollDeduction>>> getPayrollByMonthAndYear(
            @PathVariable Integer month, @PathVariable Integer year) {
        List<PayrollDeduction> payrolls = payrollService.getPayrollByMonthAndYear(month, year);
        return ResponseEntity.ok(ApiResponse.success("Payrolls retrieved successfully", payrolls));
    }
    
    @GetMapping("/my-payslips")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Get my payslips", description = "Get payslips for currently logged-in employee")
    public ResponseEntity<ApiResponse<List<PayrollDeduction>>> getMyPayslips() {
        try {
            List<PayrollDeduction> payslips = payrollService.getMyPayslips();
            return ResponseEntity.ok(ApiResponse.success("Payslips retrieved successfully", payslips));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve payroll", description = "Approve payroll and change status to PAID (ADMIN only)")
    public ResponseEntity<ApiResponse<PayrollDeduction>> approvePayroll(@PathVariable Long id) {
        try {
            PayrollDeduction payroll = payrollService.approvePayroll(id);
            return ResponseEntity.ok(ApiResponse.success("Payroll approved successfully", payroll));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/messages")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all payroll messages", description = "Retrieve all payroll notification messages")
    public ResponseEntity<ApiResponse<List<PayrollMessage>>> getAllMessages() {
        List<PayrollMessage> messages = payrollService.getAllMessages();
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved successfully", messages));
    }
    
    @GetMapping("/messages/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get messages by employee", description = "Retrieve payroll messages for a specific employee")
    public ResponseEntity<ApiResponse<List<PayrollMessage>>> getMessagesByEmployeeId(@PathVariable Long employeeId) {
        List<PayrollMessage> messages = payrollService.getMessagesByEmployeeId(employeeId);
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved successfully", messages));
    }
}
