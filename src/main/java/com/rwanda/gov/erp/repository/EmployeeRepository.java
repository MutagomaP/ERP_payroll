package com.rwanda.gov.erp.repository;

import com.rwanda.gov.erp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByCode(String code);
    boolean existsByEmail(String email);
    boolean existsByCode(String code);
    boolean existsByRoles(String roles);
}
