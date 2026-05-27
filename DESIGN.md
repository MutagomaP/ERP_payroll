# ERP Payroll Management System - Design Document

## 1. System Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.0
- **Database**: MySQL
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT
- **API Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Maven
- **Java Version**: 17

### Architecture Pattern - Layered Architecture
```
Client → JWT Filter → Controller → Service → Repository → Database
```

## 2. Database ERD Schema

### Tables and Relationships

#### employee (User Authentication)
- id, code, first_name, last_name, email, password, roles, mobile, date_of_birth, status

#### employment (Employment Details)
- id, code, employee_id (FK), department, position, base_salary, status, joining_date

#### deduction_rate (Tax & Deduction Percentages)
- id, code, deduction_name, percentage, is_active

#### payroll_deduction (Monthly Payroll)
- id, code, employment_id (FK), base_salary, housing_allowance, transport_allowance
- gross_salary, employee_tax_amount, pension_amount, medical_insurance_amount
- other_deduction_amount, net_salary, month, year, status
- UNIQUE(employment_id, month, year)

#### payroll_message (Email Notifications)
- id, payroll_deduction_id (FK), employee_email, message_content, sent_status, sent_at

## 3. Payroll Calculation Formula

```
Housing = Base Salary × 14%
Transport = Base Salary × 14%
Gross Salary = Base Salary + Housing + Transport

Employee Tax = Base Salary × 30%
Pension = Base Salary × 6%
Medical Insurance = Base Salary × 5%
Other Deductions = Base Salary × 6% (Cash Advance 5% + Sinking Fund 1%)

Net Salary = Gross Salary - (Tax + Pension + Medical + Other)
```

## 4. API Endpoints

### Authentication
- POST /api/auth/register
- POST /api/auth/login

### Employees (ROLE_MANAGER, ROLE_ADMIN)
- GET/POST /api/employees
- GET/PUT/DELETE /api/employees/{id}
- GET /api/employees/me

### Employment (ROLE_MANAGER, ROLE_ADMIN)
- GET/POST /api/employments
- GET/PUT /api/employments/{id}

### Deduction Rates (ROLE_ADMIN)
- GET/POST /api/deduction-rates
- GET/PUT/DELETE /api/deduction-rates/{id}

### Payroll
- POST /api/payroll/generate (ROLE_MANAGER)
- GET /api/payroll (ROLE_MANAGER, ROLE_ADMIN)
- GET /api/payroll/my-payslips (ROLE_EMPLOYEE)
- PUT /api/payroll/{id}/approve (ROLE_ADMIN)
- GET /api/payroll/{id}/payslip

## 5. Security Roles

- **ROLE_ADMIN**: Approve payroll, view all data
- **ROLE_MANAGER**: Generate payroll, manage employees
- **ROLE_EMPLOYEE**: View own payslips only
