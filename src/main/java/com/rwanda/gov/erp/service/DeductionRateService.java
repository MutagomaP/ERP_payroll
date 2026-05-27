package com.rwanda.gov.erp.service;

import com.rwanda.gov.erp.dto.request.DeductionRateRequest;
import com.rwanda.gov.erp.entity.DeductionRate;
import com.rwanda.gov.erp.repository.DeductionRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionRateService {
    
    private final DeductionRateRepository deductionRateRepository;
    
    public List<DeductionRate> getAllDeductionRates() {
        return deductionRateRepository.findAll();
    }
    
    public DeductionRate getDeductionRateById(Long id) {
        return deductionRateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction rate not found with id: " + id));
    }
    
    @Transactional
    public DeductionRate createDeductionRate(DeductionRateRequest request) {
        if (deductionRateRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Deduction rate code already exists");
        }
        
        DeductionRate deductionRate = new DeductionRate();
        deductionRate.setCode(request.getCode());
        deductionRate.setDeductionName(request.getDeductionName());
        deductionRate.setPercentage(request.getPercentage());
        deductionRate.setIsActive(true);
        
        return deductionRateRepository.save(deductionRate);
    }
    
    @Transactional
    public DeductionRate updateDeductionRate(Long id, DeductionRateRequest request) {
        DeductionRate deductionRate = getDeductionRateById(id);
        
        deductionRate.setDeductionName(request.getDeductionName());
        deductionRate.setPercentage(request.getPercentage());
        
        return deductionRateRepository.save(deductionRate);
    }
    
    @Transactional
    public void deleteDeductionRate(Long id) {
        DeductionRate deductionRate = getDeductionRateById(id);
        deductionRate.setIsActive(false);
        deductionRateRepository.save(deductionRate);
    }
    
    public List<DeductionRate> getActiveDeductionRates() {
        return deductionRateRepository.findByIsActive(true);
    }
}
