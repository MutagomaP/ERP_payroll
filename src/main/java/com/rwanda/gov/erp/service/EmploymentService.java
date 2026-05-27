package com.rwanda.gov.erp.service;

import com.rwanda.gov.erp.dto.request.EmploymentRequest;
import com.rwanda.gov.erp.entity.Employee;
import com.rwanda.gov.erp.entity.Employment;
import com.rwanda.gov.erp.repository.EmployeeRepository;
import com.rwanda.gov.erp.repository.EmploymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmploymentService {
    
    private final EmploymentRepository employmentRepository;
    private final EmployeeRepository employeeRepository;
    
    public List<Employment> getAllEmployments() {
        return employmentRepository.findAll();
    }
    
    public Employment getEmploymentById(Long id) {
        return employmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employment not found with id: " + id));
    }
    
    @Transactional
    public Employment createEmployment(EmploymentRequest request) {
        if (employmentRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Employment code already exists");
        }
        
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + request.getEmployeeId()));
        
        Employment employment = new Employment();
        employment.setCode(request.getCode());
        employment.setEmployee(employee);
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setJoiningDate(request.getJoiningDate());
        employment.setStatus(Employment.EmploymentStatus.ACTIVE);
        
        return employmentRepository.save(employment);
    }
    
    @Transactional
    public Employment updateEmployment(Long id, EmploymentRequest request) {
        Employment employment = getEmploymentById(id);
        
        employment.setDepartment(request.getDepartment());
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        
        return employmentRepository.save(employment);
    }
    
    public List<Employment> getActiveEmployments() {
        return employmentRepository.findByStatus(Employment.EmploymentStatus.ACTIVE);
    }
}
