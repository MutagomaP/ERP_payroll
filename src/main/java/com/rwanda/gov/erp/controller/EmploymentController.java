package com.rwanda.gov.erp.controller;

import com.rwanda.gov.erp.dto.request.EmploymentRequest;
import com.rwanda.gov.erp.dto.response.ApiResponse;
import com.rwanda.gov.erp.entity.Employment;
import com.rwanda.gov.erp.service.EmploymentService;
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
@RequestMapping("/api/employments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Employment Management", description = "APIs for managing employment records")
public class EmploymentController {
    
    private final EmploymentService employmentService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all employments", description = "Retrieve all employment records")
    public ResponseEntity<ApiResponse<List<Employment>>> getAllEmployments() {
        List<Employment> employments = employmentService.getAllEmployments();
        return ResponseEntity.ok(ApiResponse.success("Employments retrieved successfully", employments));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get employment by ID", description = "Retrieve employment details by ID")
    public ResponseEntity<ApiResponse<Employment>> getEmploymentById(@PathVariable Long id) {
        try {
            Employment employment = employmentService.getEmploymentById(id);
            return ResponseEntity.ok(ApiResponse.success("Employment retrieved successfully", employment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create employment", description = "Create a new employment record")
    public ResponseEntity<ApiResponse<Employment>> createEmployment(@Valid @RequestBody EmploymentRequest request) {
        try {
            Employment employment = employmentService.createEmployment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Employment created successfully", employment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update employment", description = "Update employment details")
    public ResponseEntity<ApiResponse<Employment>> updateEmployment(@PathVariable Long id, 
                                                                     @Valid @RequestBody EmploymentRequest request) {
        try {
            Employment employment = employmentService.updateEmployment(id, request);
            return ResponseEntity.ok(ApiResponse.success("Employment updated successfully", employment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
