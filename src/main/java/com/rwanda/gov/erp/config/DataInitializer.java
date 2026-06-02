package com.rwanda.gov.erp.config;

import com.rwanda.gov.erp.entity.Employee;
import com.rwanda.gov.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedManager();
    }

    private void seedAdmin() {
        if (!employeeRepository.existsByRoles("ROLE_ADMIN") && !employeeRepository.existsByEmail("admin@gov.rw")) {
            Employee admin = new Employee();
            admin.setCode("ADM001");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@gov.rw");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles("ROLE_ADMIN");
            admin.setMobile("+250788111111");
            admin.setDateOfBirth(LocalDate.of(1985, 1, 1));
            admin.setStatus(Employee.EmployeeStatus.ACTIVE);
            employeeRepository.save(admin);
            System.out.println("[DataInitializer] Seeded Admin User: admin@gov.rw");
        }
    }

    private void seedManager() {
        if (!employeeRepository.existsByEmail("manager@gov.rw")) {
            Employee manager = new Employee();
            manager.setCode("MGR001");
            manager.setFirstName("Manager");
            manager.setLastName("User");
            manager.setEmail("manager@gov.rw");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRoles("ROLE_MANAGER");
            manager.setMobile("+250788222222");
            manager.setDateOfBirth(LocalDate.of(1990, 1, 1));
            manager.setStatus(Employee.EmployeeStatus.ACTIVE);
            employeeRepository.save(manager);
            System.out.println("[DataInitializer] Seeded Manager User: manager@gov.rw");
        }
    }
}
