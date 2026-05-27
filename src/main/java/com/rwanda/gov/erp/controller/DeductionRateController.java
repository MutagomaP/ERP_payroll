package com.rwanda.gov.erp.controller;

import com.rwanda.gov.erp.dto.request.DeductionRateRequest;
import com.rwanda.gov.erp.dto.response.ApiResponse;
import com.rwanda.gov.erp.entity.DeductionRate;
import com.rwanda.gov.erp.service.DeductionRateService;
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
@RequestMapping("/api/deduction-rates")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Deduction Rate Management", description = "APIs for managing tax and deduction rates")
public class DeductionRateController {
    
    private final DeductionRateService deductionRateService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all deduction rates", description = "Retrieve all deduction rates")
    public ResponseEntity<ApiResponse<List<DeductionRate>>> getAllDeductionRates() {
        List<DeductionRate> rates = deductionRateService.getAllDeductionRates();
        return ResponseEntity.ok(ApiResponse.success("Deduction rates retrieved successfully", rates));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get deduction rate by ID", description = "Retrieve deduction rate by ID")
    public ResponseEntity<ApiResponse<DeductionRate>> getDeductionRateById(@PathVariable Long id) {
        try {
            DeductionRate rate = deductionRateService.getDeductionRateById(id);
            return ResponseEntity.ok(ApiResponse.success("Deduction rate retrieved successfully", rate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create deduction rate", description = "Create a new deduction rate")
    public ResponseEntity<ApiResponse<DeductionRate>> createDeductionRate(@Valid @RequestBody DeductionRateRequest request) {
        try {
            DeductionRate rate = deductionRateService.createDeductionRate(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Deduction rate created successfully", rate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update deduction rate", description = "Update deduction rate details")
    public ResponseEntity<ApiResponse<DeductionRate>> updateDeductionRate(@PathVariable Long id, 
                                                                           @Valid @RequestBody DeductionRateRequest request) {
        try {
            DeductionRate rate = deductionRateService.updateDeductionRate(id, request);
            return ResponseEntity.ok(ApiResponse.success("Deduction rate updated successfully", rate));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete deduction rate", description = "Deactivate a deduction rate")
    public ResponseEntity<ApiResponse<Void>> deleteDeductionRate(@PathVariable Long id) {
        try {
            deductionRateService.deleteDeductionRate(id);
            return ResponseEntity.ok(ApiResponse.success("Deduction rate deactivated successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
