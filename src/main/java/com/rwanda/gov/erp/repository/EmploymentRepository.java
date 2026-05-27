package com.rwanda.gov.erp.repository;

import com.rwanda.gov.erp.entity.Employment;
import com.rwanda.gov.erp.entity.Employment.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
    Optional<Employment> findByCode(String code);
    Optional<Employment> findByEmployeeId(Long employeeId);
    List<Employment> findByStatus(EmploymentStatus status);
    boolean existsByCode(String code);
}
