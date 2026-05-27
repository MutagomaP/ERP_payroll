package com.rwanda.gov.erp.repository;

import com.rwanda.gov.erp.entity.PayrollMessage;
import com.rwanda.gov.erp.entity.PayrollMessage.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollMessageRepository extends JpaRepository<PayrollMessage, Long> {
    List<PayrollMessage> findBySentStatus(MessageStatus sentStatus);
    
    @Query("SELECT m FROM PayrollMessage m WHERE m.payrollDeduction.employment.employee.id = :employeeId")
    List<PayrollMessage> findByEmployeeId(@Param("employeeId") Long employeeId);
}
