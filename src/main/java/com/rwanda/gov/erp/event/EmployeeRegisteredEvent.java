package com.rwanda.gov.erp.event;

public record EmployeeRegisteredEvent(
        String email,
        String firstName,
        String lastName,
        String code,
        String roles
) {
}
