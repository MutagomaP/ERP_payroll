package com.rwanda.gov.erp.repository;

import com.rwanda.gov.erp.entity.PayrollDeduction;
import com.rwanda.gov.erp.entity.PayrollDeduction.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollDeductionRepository extends JpaRepository<PayrollDeduction, Long> {
    Optional<PayrollDeduction> findByCode(String code);
    
    @Query("SELECT p FROM PayrollDeduction p WHERE p.employment.id = :employmentId AND p.month = :month AND p.year = :year")
    Optional<PayrollDeduction> findByEmploymentIdAndMonthAndYear(
        @Param("employmentId") Long employmentId, 
        @Param("month") Integer month, 
        @Param("year") Integer year
    );
    
    List<PayrollDeduction> findByMonthAndYear(Integer month, Integer year);
    
    @Query("SELECT p FROM PayrollDeduction p WHERE p.employment.employee.id = :employeeId")
    List<PayrollDeduction> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    List<PayrollDeduction> findByStatus(PayrollStatus status);
}
