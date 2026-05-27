package com.rwanda.gov.erp.listener;

import com.rwanda.gov.erp.event.EmployeeRegisteredEvent;
import com.rwanda.gov.erp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RegistrationEmailListener {

    private final EmailService emailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEmployeeRegistered(EmployeeRegisteredEvent event) {
        emailService.sendRegistrationWelcomeEmail(event);
    }
}
