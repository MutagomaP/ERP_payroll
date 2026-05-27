package com.rwanda.gov.erp.repository;

import com.rwanda.gov.erp.entity.DeductionRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeductionRateRepository extends JpaRepository<DeductionRate, Long> {
    Optional<DeductionRate> findByCode(String code);
    Optional<DeductionRate> findByDeductionName(String deductionName);
    List<DeductionRate> findByIsActive(Boolean isActive);
    boolean existsByCode(String code);
}
