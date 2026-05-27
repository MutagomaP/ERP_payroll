package com.rwanda.gov.erp.service;

import com.rwanda.gov.erp.event.EmployeeRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from}")
    private String fromAddress;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationWelcomeEmail(EmployeeRegisteredEvent event) {
        if (!mailEnabled) {
            log.debug("Registration email skipped (app.mail.enabled=false) for {}", event.email());
            return;
        }
        if (mailSender == null) {
            log.warn("Registration email skipped: JavaMailSender not configured (set spring.mail.host)");
            return;
        }
        if (!StringUtils.hasText(fromAddress)) {
            log.warn("Registration email skipped: app.mail.from is not set");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(event.email());
            message.setSubject("Welcome to ERP Payroll Management System");
            message.setText(buildRegistrationBody(event));
            mailSender.send(message);
            log.info("Registration welcome email sent to {}", event.email());
        } catch (Exception ex) {
            log.error("Failed to send registration email to {}", event.email(), ex);
        }
    }

    private String buildRegistrationBody(EmployeeRegisteredEvent event) {
        return String.format(
                """
                Dear %s %s,

                Your account has been created on the Rwanda Government ERP Payroll Management System.

                Employee code: %s
                Email: %s
                Role: %s

                You can sign in with the email and password you registered.

                Regards,
                ERP Payroll Team
                """,
                event.firstName(),
                event.lastName(),
                event.code(),
                event.email(),
                event.roles()
        );
    }
}
