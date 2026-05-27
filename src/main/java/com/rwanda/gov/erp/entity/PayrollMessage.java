package com.rwanda.gov.erp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayrollMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_deduction_id", nullable = false)
    private PayrollDeduction payrollDeduction;
    
    @Column(name = "employee_email", nullable = false)
    private String employeeEmail;
    
    @Column(name = "message_content", columnDefinition = "TEXT")
    private String messageContent;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sent_status", nullable = false)
    private MessageStatus sentStatus = MessageStatus.PENDING;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    public enum MessageStatus {
        PENDING, SENT, FAILED
    }
}
