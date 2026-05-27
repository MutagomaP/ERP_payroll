package com.rwanda.gov.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ErpPayrollApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ErpPayrollApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("ERP Payroll Management System Started!");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("API Docs: http://localhost:8080/api-docs");
        System.out.println("==============================================\n");
    }
}
