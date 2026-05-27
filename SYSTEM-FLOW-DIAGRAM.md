# ERP Payroll System - Flow Diagrams

## 1. Spring Boot Application Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT REQUEST                          │
│                    (Postman / Swagger / Browser)                │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   JWT AUTHENTICATION FILTER                     │
│  • Extract JWT token from Authorization header                  │
│  • Validate token signature and expiration                      │
│  • Load user details and set authentication context             │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    SPRING SECURITY FILTER                       │
│  • Check if user is authenticated                               │
│  • Verify user has required role (ADMIN/MANAGER/EMPLOYEE)       │
│  • Allow or deny access based on endpoint permissions           │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      CONTROLLER LAYER                           │
│  • AuthController - /api/auth/*                                 │
│  • EmployeeController - /api/employees/*                        │
│  • EmploymentController - /api/employments/*                    │
│  • DeductionRateController - /api/deduction-rates/*             │
│  • PayrollController - /api/payroll/*                           │
│  • Validate request body                                        │
│  • Call appropriate service method                              │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                       SERVICE LAYER                             │
│  • AuthService - Registration, Login, JWT generation            │
│  • EmployeeService - Employee CRUD operations                   │
│  • EmploymentService - Employment CRUD operations               │
│  • DeductionRateService - Deduction rate management             │
│  • PayrollService - Payroll generation and approval             │
│  • Business logic validation                                    │
│  • Transaction management                                       │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                     REPOSITORY LAYER                            │
│  • EmployeeRepository - JPA operations on employee table        │
│  • EmploymentRepository - JPA operations on employment table    │
│  • DeductionRateRepository - JPA operations on deduction_rate   │
│  • PayrollDeductionRepository - JPA operations on payroll       │
│  • PayrollMessageRepository - JPA operations on messages        │
│  • Spring Data JPA auto-implementation                          │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      HIBERNATE (JPA)                            │
│  • Convert Java objects to SQL queries                          │
│  • Manage database connections                                  │
│  • Handle transactions                                          │
│  • Cache management                                             │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      MYSQL DATABASE                             │
│  • employee table                                               │
│  • employment table                                             │
│  • deduction_rate table                                         │
│  • payroll_deduction table                                      │
│  • payroll_message table                                        │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      RESPONSE (JSON)                            │
│  • Success/Error status                                         │
│  • Message                                                      │
│  • Data payload                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Authentication Flow

```
┌──────────┐
│  Client  │
└────┬─────┘
     │
     │ 1. POST /api/auth/register
     │    {email, password, roles, ...}
     ▼
┌─────────────────┐
│ AuthController  │
└────┬────────────┘
     │
     │ 2. Call authService.register()
     ▼
┌─────────────────┐
│  AuthService    │
│  • Validate     │
│  • Hash pwd     │
│  • Save to DB   │
└────┬────────────┘
     │
     │ 3. Save employee
     ▼
┌─────────────────┐
│EmployeeRepo     │
└────┬────────────┘
     │
     │ 4. Return success
     ▼
┌──────────┐
│  Client  │
└────┬─────┘
     │
     │ 5. POST /api/auth/login
     │    {email, password}
     ▼
┌─────────────────┐
│ AuthController  │
└────┬────────────┘
     │
     │ 6. Authenticate
     ▼
┌─────────────────────────┐
│ AuthenticationManager   │
│  • Load user details    │
│  • Verify password      │
└────┬────────────────────┘
     │
     │ 7. Generate JWT token
     ▼
┌─────────────────┐
│ JwtTokenProvider│
└────┬────────────┘
     │
     │ 8. Return token
     ▼
┌──────────┐
│  Client  │
│ (Stores  │
│  token)  │
└──────────┘
```

---

## 3. Payroll Generation Flow

```
┌──────────┐
│ Manager  │
└────┬─────┘
     │
     │ 1. POST /api/payroll/generate
     │    Authorization: Bearer <token>
     │    {month: 6, year: 2025}
     ▼
┌─────────────────────┐
│ JWT Filter          │
│ • Validate token    │
│ • Set auth context  │
└────┬────────────────┘
     │
     │ 2. Check ROLE_MANAGER
     ▼
┌─────────────────────┐
│ Security Filter     │
│ • Verify role       │
└────┬────────────────┘
     │
     │ 3. Call controller
     ▼
┌─────────────────────┐
│ PayrollController   │
└────┬────────────────┘
     │
     │ 4. Call service
     ▼
┌─────────────────────────────────────────┐
│ PayrollService.generatePayroll()        │
│                                         │
│ 5. Get all ACTIVE employments           │
│    ↓                                    │
│ 6. For each employment:                 │
│    • Check employee is ACTIVE           │
│    • Check no duplicate payroll         │
│    • Calculate payroll:                 │
│      - Housing = base × 14%             │
│      - Transport = base × 14%           │
│      - Gross = base + housing + trans   │
│      - Tax = base × 30%                 │
│      - Pension = base × 6%              │
│      - Medical = base × 5%              │
│      - Other = base × 6%                │
│      - Net = Gross - deductions         │
│    • Validate deductions ≤ gross        │
│    • Save payroll (status: PENDING)     │
│    ↓                                    │
│ 7. Return list of payrolls              │
└────┬────────────────────────────────────┘
     │
     │ 8. Return response
     ▼
┌──────────┐
│ Manager  │
│ (Receives│
│ payroll  │
│  list)   │
└──────────┘
```

---

## 4. Payroll Approval Flow

```
┌──────────┐
│  Admin   │
└────┬─────┘
     │
     │ 1. PUT /api/payroll/{id}/approve
     │    Authorization: Bearer <token>
     ▼
┌─────────────────────┐
│ JWT Filter          │
│ • Validate token    │
└────┬────────────────┘
     │
     │ 2. Check ROLE_ADMIN
     ▼
┌─────────────────────┐
│ Security Filter     │
└────┬────────────────┘
     │
     │ 3. Call controller
     ▼
┌─────────────────────┐
│ PayrollController   │
└────┬────────────────┘
     │
     │ 4. Call service
     ▼
┌─────────────────────────────────────────┐
│ PayrollService.approvePayroll(id)       │
│                                         │
│ 5. Get payroll by ID                    │
│    ↓                                    │
│ 6. Check status is PENDING              │
│    ↓                                    │
│ 7. Update status to PAID                │
│    ↓                                    │
│ 8. Save payroll                         │
│    ↓                                    │
│ 9. Generate message:                    │
│    • Get employee details               │
│    • Create message content:            │
│      "Dear [Name], Your salary of       │
│       [Month]/[Year] from Rwanda        │
│       Government RWF [Amount] has       │
│       been credited to your [Code]      │
│       account successfully."            │
│    • Save message (status: PENDING)     │
│    ↓                                    │
│ 10. Return approved payroll             │
└────┬────────────────────────────────────┘
     │
     │ 11. Return response
     ▼
┌──────────┐
│  Admin   │
│ (Payroll │
│ approved)│
└──────────┘
```

---

## 5. Database Entity Relationships

```
┌─────────────────────┐
│     EMPLOYEE        │
│ ─────────────────── │
│ PK: id              │
│ UK: code            │
│ UK: email           │
│     first_name      │
│     last_name       │
│     password        │
│     roles           │
│     mobile          │
│     date_of_birth   │
│     status          │
└──────────┬──────────┘
           │
           │ 1
           │
           │ *
┌──────────▼──────────┐
│    EMPLOYMENT       │
│ ─────────────────── │
│ PK: id              │
│ UK: code            │
│ FK: employee_id     │
│     department      │
│     position        │
│     base_salary     │
│     status          │
│     joining_date    │
└──────────┬──────────┘
           │
           │ 1
           │
           │ *
┌──────────▼──────────────────┐
│   PAYROLL_DEDUCTION         │
│ ─────────────────────────── │
│ PK: id                      │
│ UK: code                    │
│ FK: employment_id           │
│     base_salary             │
│     housing_allowance       │
│     transport_allowance     │
│     gross_salary            │
│     employee_tax_amount     │
│     pension_amount          │
│     medical_insurance_amt   │
│     other_deduction_amount  │
│     net_salary              │
│     month                   │
│     year                    │
│     status                  │
│ UNIQUE(employment_id,       │
│        month, year)         │
└──────────┬──────────────────┘
           │
           │ 1
           │
           │ 1
┌──────────▼──────────┐
│  PAYROLL_MESSAGE    │
│ ─────────────────── │
│ PK: id              │
│ FK: payroll_ded_id  │
│     employee_email  │
│     message_content │
│     sent_status     │
│     sent_at         │
└─────────────────────┘

┌─────────────────────┐
│  DEDUCTION_RATE     │
│ ─────────────────── │
│ PK: id              │
│ UK: code            │
│     deduction_name  │
│     percentage      │
│     is_active       │
└─────────────────────┘
(Configuration table)
```

---

## 6. Role-Based Access Control

```
┌─────────────────────────────────────────────────────────────┐
│                         ENDPOINTS                           │
└─────────────────────────────────────────────────────────────┘

/api/auth/register ──────────────────────────► PUBLIC
/api/auth/login ─────────────────────────────► PUBLIC

/api/employees/* ────────────────────────────► ADMIN, MANAGER
/api/employments/* ──────────────────────────► ADMIN, MANAGER
/api/deduction-rates/* ──────────────────────► ADMIN

/api/payroll/generate ───────────────────────► MANAGER
/api/payroll (GET) ──────────────────────────► ADMIN, MANAGER
/api/payroll/{id}/approve ───────────────────► ADMIN
/api/payroll/my-payslips ────────────────────► ALL ROLES
/api/payroll/messages ───────────────────────► ADMIN

┌─────────────────────────────────────────────────────────────┐
│                      ROLE HIERARCHY                         │
└─────────────────────────────────────────────────────────────┘

                    ┌──────────────┐
                    │  ROLE_ADMIN  │
                    │              │
                    │ • Approve    │
                    │   payroll    │
                    │ • Manage     │
                    │   deductions │
                    │ • View all   │
                    └──────┬───────┘
                           │
                    ┌──────▼────────┐
                    │ ROLE_MANAGER  │
                    │               │
                    │ • Generate    │
                    │   payroll     │
                    │ • Manage      │
                    │   employees   │
                    │ • Manage      │
                    │   employments │
                    └──────┬────────┘
                           │
                    ┌──────▼────────┐
                    │ ROLE_EMPLOYEE │
                    │               │
                    │ • View own    │
                    │   details     │
                    │ • View own    │
                    │   payslips    │
                    └───────────────┘
```

---

## 7. Payroll Calculation Formula

```
INPUT: Base Salary (e.g., 70,000 RWF)

┌─────────────────────────────────────┐
│      ALLOWANCES (Added)             │
├─────────────────────────────────────┤
│ Housing    = Base × 14% = 9,800     │
│ Transport  = Base × 14% = 9,800     │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│         GROSS SALARY                │
│  Base + Housing + Transport         │
│  70,000 + 9,800 + 9,800 = 89,600    │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│      DEDUCTIONS (Subtracted)        │
├─────────────────────────────────────┤
│ Tax        = Base × 30% = 21,000    │
│ Pension    = Base × 6%  = 4,200     │
│ Medical    = Base × 5%  = 3,500     │
│ Other      = Base × 6%  = 4,200     │
│ (Cash Advance 5% + Sinking Fund 1%) │
│                                     │
│ Total Deductions = 32,900           │
└─────────────────┬───────────────────┘
                  │
                  ▼
┌─────────────────────────────────────┐
│          NET SALARY                 │
│  Gross - Total Deductions           │
│  89,600 - 32,900 = 57,400 RWF       │
└─────────────────────────────────────┘

OUTPUT: Net Salary (57,400 RWF)
```

---

## 8. System Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRODUCTION ENVIRONMENT                   │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐
│   Clients    │
│ (Browsers,   │
│  Mobile Apps)│
└──────┬───────┘
       │
       │ HTTPS
       ▼
┌──────────────┐
│ Load Balancer│
└──────┬───────┘
       │
       ▼
┌─────────────────────────────────────┐
│   Spring Boot Application Servers   │
│  ┌─────────┐  ┌─────────┐          │
│  │ Server 1│  │ Server 2│  ...     │
│  └─────────┘  └─────────┘          │
│  • JWT Auth                         │
│  • Business Logic                   │
│  • API Endpoints                    │
└──────┬──────────────────────────────┘
       │
       │ JDBC
       ▼
┌──────────────┐
│ MySQL Master │
└──────┬───────┘
       │
       │ Replication
       ▼
┌──────────────┐
│ MySQL Slave  │
│  (Read Only) │
└──────────────┘
```

---

**All diagrams represent the actual implementation in the ERP Payroll System**
