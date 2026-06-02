package com.rwanda.gov.erp.service;

import com.rwanda.gov.erp.dto.request.LoginRequest;
import com.rwanda.gov.erp.dto.request.RegisterRequest;
import com.rwanda.gov.erp.dto.response.AuthResponse;
import com.rwanda.gov.erp.entity.Employee;
import com.rwanda.gov.erp.event.EmployeeRegisteredEvent;
import com.rwanda.gov.erp.repository.EmployeeRepository;
import com.rwanda.gov.erp.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        if (employeeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Employee code already exists");
        }

        if (ROLE_ADMIN.equals(request.getRoles()) && employeeRepository.existsByRoles(ROLE_ADMIN)) {
            throw new RuntimeException("Only one admin is allowed in the system");
        }

        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBefore(LocalDate.now())) {
            throw new RuntimeException("Date of birth must be in the past");
        }
        
        Employee employee = new Employee();
        employee.setCode(request.getCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setRoles(request.getRoles());
        employee.setMobile(request.getMobile());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setStatus(Employee.EmployeeStatus.ACTIVE);
        
        employeeRepository.save(employee);

        eventPublisher.publishEvent(new EmployeeRegisteredEvent(
                employee.getEmail(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getCode(),
                employee.getRoles()
        ));
        
        return new AuthResponse(null, employee.getEmail(), employee.getRoles(), 
                "Employee registered successfully");
    }
    
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new AuthResponse(token, employee.getEmail(), employee.getRoles(), 
                "Login successful");
    }
}
