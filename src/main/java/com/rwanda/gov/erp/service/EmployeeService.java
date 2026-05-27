package com.rwanda.gov.erp.service;

import com.rwanda.gov.erp.entity.Employee;
import com.rwanda.gov.erp.repository.EmployeeRepository;
import com.rwanda.gov.erp.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
    
    public Employee getCurrentEmployee() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return employeeRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new RuntimeException("Current employee not found"));
    }
    
    @Transactional
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);
        
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setMobile(employeeDetails.getMobile());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        employee.setStatus(employeeDetails.getStatus());
        
        return employeeRepository.save(employee);
    }
    
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employee.setStatus(Employee.EmployeeStatus.DISABLED);
        employeeRepository.save(employee);
    }
}
