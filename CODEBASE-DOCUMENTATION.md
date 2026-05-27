# ERP Payroll Management System — Codebase Documentation

This document explains how the project is organized, how requests flow through the application, and how the main business rules are implemented. It is intended for developers, examiners, and anyone maintaining or extending the system.

**Related documents**

| File | Purpose |
|------|---------|
| [README.md](README.md) | Setup, API examples, payroll formulas |
| [TESTING-GUIDE.md](TESTING-GUIDE.md) | Step-by-step API testing |
| [SYSTEM-FLOW-DIAGRAM.md](SYSTEM-FLOW-DIAGRAM.md) | Visual flows and sequence diagrams |
| [QUICK-START.md](QUICK-START.md) | Fast local run instructions |
| [ERP-Payroll-Postman-Collection.json](ERP-Payroll-Postman-Collection.json) | Importable Postman requests |

---

## 1. What this application does

The **ERP Payroll Management System** is a REST API for Rwanda Government–style payroll operations. It supports:

- **Employee accounts** with roles (Admin, Manager, Employee)
- **Employment records** (department, position, base salary)
- **Configurable deduction rates** (stored in the database; reference data)
- **Automated payroll generation** for a given month/year
- **Payroll approval** and **notification messages** when salary is marked paid
- **JWT authentication** and **role-based access control**

There is no separate frontend in this repository; clients are Postman, Swagger UI, or any HTTP client.

**Base URL (local):** `http://localhost:8080`

---

## 2. Technology stack

| Layer | Technology |
|-------|------------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Web | Spring Web (REST) |
| Persistence | Spring Data JPA (Hibernate) |
| Database | PostgreSQL |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Validation | Jakarta Bean Validation (`@Valid`) |
| API docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven |
| Utilities | Lombok |

Main entry point: `com.rwanda.gov.erp.ErpPayrollApplication`

---

## 3. Project structure

```
Java practical/
├── pom.xml                          # Maven dependencies and build
├── src/main/
│   ├── java/com/rwanda/gov/erp/
│   │   ├── ErpPayrollApplication.java   # Boot entry point
│   │   ├── config/                      # Security, Swagger
│   │   ├── controller/                  # REST endpoints (HTTP layer)
│   │   ├── service/                     # Business logic
│   │   ├── repository/                  # Database access (JPA)
│   │   ├── entity/                      # JPA entities (tables)
│   │   ├── dto/
│   │   │   ├── request/                 # Incoming JSON bodies
│   │   │   └── response/                # API wrappers (ApiResponse, AuthResponse)
│   │   ├── security/                    # JWT filter, UserDetails, token provider
│   │   └── exception/                   # Global error handling
│   └── resources/
│       ├── application.properties       # DB, JWT, Swagger, port
│       ├── init-deduction-rates.sql     # Optional seed for deduction_rate table
│       └── database-trigger.sql         # Optional PostgreSQL trigger (see §8)
└── (docs) README.md, TESTING-GUIDE.md, SYSTEM-FLOW-DIAGRAM.md, ...
```

### Layered architecture

```
  Client (Postman / Swagger)
           │
           ▼
  ┌─────────────────────┐
  │     Controller      │  HTTP mapping, validation, ResponseEntity
  └──────────┬──────────┘
             ▼
  ┌─────────────────────┐
  │      Service        │  Business rules, transactions
  └──────────┬──────────┘
             ▼
  ┌─────────────────────┐
  │    Repository       │  Spring Data JPA queries
  └──────────┬──────────┘
             ▼
  ┌─────────────────────┐
  │  Entity / PostgreSQL │
  └─────────────────────┘
```

**Security** sits across the pipeline: `JwtAuthenticationFilter` runs before controllers; `SecurityConfig` and `@PreAuthorize` enforce roles.

---

## 4. Database model

Hibernate creates/updates tables when `spring.jpa.hibernate.ddl-auto=update` (see `application.properties`).

### Entity relationship (logical)

```
┌─────────────┐       ┌──────────────┐       ┌──────────────────┐
│  Employee   │ 1───* │  Employment  │ 1───* │ PayrollDeduction │
│  (login)    │       │  (job/salary)│       │  (payslip)       │
└─────────────┘       └──────────────┘       └────────┬─────────┘
                                                      │ 1
                                                      │
                                                      ▼ 1
                                              ┌──────────────────┐
                                              │ PayrollMessage   │
                                              └──────────────────┘

┌──────────────┐
│ DeductionRate│  (standalone reference table; not FK-linked to payroll calc)
└──────────────┘
```

### Tables and responsibilities

| Entity | Table | Purpose |
|--------|-------|---------|
| `Employee` | `employee` | User account: name, email, BCrypt password, role string, status |
| `Employment` | `employment` | Job linked to employee: department, position, base salary, joining date |
| `DeductionRate` | `deduction_rate` | Named percentage rates (CRUD by Admin); used for configuration/reference |
| `PayrollDeduction` | `payroll_deduction` | One payslip per employment + month + year (unique constraint) |
| `PayrollMessage` | `payroll_message` | Notification text when payroll is approved |

### Important constraints

- **One payslip per employment per month/year** — `@UniqueConstraint` on `(employment_id, month, year)` in `PayrollDeduction`.
- **One admin in the system** — enforced in `AuthService.register()` when `roles` is `ROLE_ADMIN`.
- **Soft delete for employees** — `DELETE /api/employees/{id}` sets `status = DISABLED`; disabled users cannot log in.
- **Deduction rates “delete”** — sets `isActive = false`, does not remove the row.

---

## 5. Security and authentication

### Public endpoints (no JWT)

- `POST /api/auth/register`
- `POST /api/auth/login`
- Swagger/OpenAPI paths (`/swagger-ui/**`, `/v3/api-docs/**`, etc.)

### Protected endpoints

All other `/api/**` routes require a valid JWT in the header:

```http
Authorization: Bearer <token>
```

### How JWT works

1. **Login** — `AuthService` uses `AuthenticationManager` to verify email/password.
2. **Token** — `JwtTokenProvider` builds a signed JWT (subject = email, expiry from `jwt.expiration`, default 1 hour).
3. **Each request** — `JwtAuthenticationFilter` reads `Authorization`, validates token, loads `UserDetailsImpl` from DB, sets `SecurityContextHolder`.
4. **Authorization** — Two mechanisms work together:
   - **URL rules** in `SecurityConfig` (e.g. `/api/deduction-rates/**` → ADMIN only)
   - **Method rules** via `@PreAuthorize` on controller methods (e.g. `hasRole('MANAGER')` on payroll generate)

### Roles

Stored on `Employee.roles` as a string, typically one of:

| Value | Spring authority | Typical permissions |
|-------|------------------|---------------------|
| `ROLE_ADMIN` | `ROLE_ADMIN` | Approve payroll, manage deduction rates, view messages; **only one allowed** |
| `ROLE_MANAGER` | `ROLE_MANAGER` | Generate payroll, manage employees/employments |
| `ROLE_EMPLOYEE` | `ROLE_EMPLOYEE` | View own profile and payslips |

`UserDetailsImpl.build()` splits `roles` by comma if multiple authorities are ever stored.

Passwords are hashed with **BCrypt** (`PasswordEncoder` bean in `SecurityConfig`).

---

## 6. Package-by-package reference

### 6.1 `controller` — REST API surface

| Class | Base path | Responsibility |
|-------|-----------|----------------|
| `AuthController` | `/api/auth` | Register, login |
| `EmployeeController` | `/api/employees` | List/get/update/disable employees; `/me` for current user |
| `EmploymentController` | `/api/employments` | CRUD for employment records |
| `DeductionRateController` | `/api/deduction-rates` | CRUD for deduction rates (Admin) |
| `PayrollController` | `/api/payroll` | Generate, list, approve payroll; payslips; messages |

Controllers are thin: they delegate to services and wrap results in `ApiResponse<T>`:

```json
{
  "success": true,
  "message": "Human-readable message",
  "data": { }
}
```

### 6.2 `service` — Business logic

| Class | Key behavior |
|-------|----------------|
| `AuthService` | Register (duplicate email/code checks, **single admin**), login (JWT) |
| `EmployeeService` | CRUD helpers; `getCurrentEmployee()` from security context; delete = disable |
| `EmploymentService` | Link employment to employee; update department/position/salary |
| `DeductionRateService` | Manage `deduction_rate` rows; soft deactivate on delete |
| `PayrollService` | **Core payroll math**, generate/approve, message creation |

### 6.3 `repository` — Data access

Spring Data `JpaRepository` interfaces with custom `@Query` methods where needed, e.g.:

- `PayrollDeductionRepository.findByEmploymentIdAndMonthAndYear` — duplicate payroll check
- `PayrollDeductionRepository.findByEmployeeId` — “my payslips”
- `EmployeeRepository.existsByRoles` — single-admin check

### 6.4 `entity` — JPA models

Map 1:1 to database tables. Use Lombok `@Data`, Hibernate `@CreationTimestamp` / `@UpdateTimestamp` where applicable.

`Employee` doubles as the **security principal** (email = username).

### 6.5 `dto`

- **`request`** — Validated input (`LoginRequest`, `RegisterRequest`, `EmploymentRequest`, `DeductionRateRequest`, `GeneratePayrollRequest`).
- **`response`** — `ApiResponse` wrapper, `AuthResponse` (token + email + roles).

Controllers use `@Valid` on request DTOs; validation errors are handled by `GlobalExceptionHandler`.

### 6.6 `security`

| Class | Role |
|-------|------|
| `JwtTokenProvider` | Create/parse/validate JWT |
| `JwtAuthenticationFilter` | Extract Bearer token per request |
| `UserDetailsServiceImpl` | Load employee by email; reject DISABLED |
| `UserDetailsImpl` | Spring `UserDetails` + employee id |

### 6.7 `config`

| Class | Role |
|-------|------|
| `SecurityConfig` | Stateless sessions, CSRF off, URL authorization, JWT filter chain |
| `SwaggerConfig` | OpenAPI metadata, Bearer scheme, tag ordering |

### 6.8 `exception`

`GlobalExceptionHandler` (`@RestControllerAdvice`) maps:

- `MethodArgumentNotValidException` → 400 with field errors in `data`
- `RuntimeException` → 400 with message (most business errors)
- `BadCredentialsException` → 401
- `AccessDeniedException` → 403
- Generic `Exception` → 500

---

## 7. Payroll calculation (implementation detail)

Payroll logic lives in **`PayrollService`**. Rates are **hardcoded constants** in that class (not read from `deduction_rate` at runtime):

| Component | Rate (% of base salary) |
|-----------|-------------------------|
| Housing allowance | 14% |
| Transport allowance | 14% |
| Employee tax | 30% |
| Pension | 6% |
| Medical insurance | 5% |
| Other (cash advance + sinking fund) | 5% + 1% = 6% |

**Formulas:**

```
housingAllowance   = base × 14%
transportAllowance = base × 14%
grossSalary        = base + housing + transport

employeeTax        = base × 30%
pension            = base × 6%
medicalInsurance   = base × 5%
otherDeductions    = base × 6%

totalDeductions    = tax + pension + medical + other
netSalary          = gross − totalDeductions
```

Rounding: `HALF_UP`, 2 decimal places.

**Generate payroll** (`POST /api/payroll/generate`):

1. Load all employments with status `ACTIVE`.
2. Skip if linked employee is not `ACTIVE`.
3. Abort if a payslip already exists for that employment + month + year.
4. Abort if total deductions exceed gross salary.
5. Save `PayrollDeduction` with status `PENDING` and a generated code `PAY-XXXXXXXX`.

**Approve payroll** (`PUT /api/payroll/{id}/approve`):

1. Set status to `PAID`.
2. Call `generatePayrollMessage()` — inserts `PayrollMessage` with formatted text and `sentStatus = PENDING`.

The `deduction_rate` table and `DeductionRateService` support **admin configuration and reporting**; changing those rows does **not** automatically change `PayrollService` calculations unless you refactor the service to read from the DB.

---

## 8. Messages and database trigger

**Application path (active):** Messages are created in Java when Admin approves payroll (`PayrollService.approvePayroll`).

**Optional database path:** `src/main/resources/database-trigger.sql` defines a PostgreSQL trigger on `payroll_deduction` that could insert into `payroll_message` when status changes `PENDING` → `PAID`. The SQL file notes this is **not wired by Hibernate**; if you install the trigger manually, you could get **duplicate messages** unless you disable the Java-side generation. For this codebase, treat **Java as the source of truth**.

Message format (example):

> Dear {firstName}, Your salary of {month}/{year} from Rwanda Government RWF {netSalary} has been credited to your {employmentCode} account successfully.

Email sending is not implemented; `sentStatus` remains `PENDING` until a future email integration.

---

## 9. API summary by module

### Authentication — `/api/auth` (no token)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/register` | Create employee; blocks second `ROLE_ADMIN` |
| POST | `/login` | Returns JWT in `data.token` |

### Employees — `/api/employees` (token required)

| Method | Path | Roles | Notes |
|--------|------|-------|-------|
| GET | `/` | ADMIN, MANAGER | All employees |
| GET | `/{id}` | ADMIN, MANAGER | By id |
| GET | `/me` | Authenticated | Current user from JWT |
| PUT | `/{id}` | ADMIN, MANAGER | Updates name, mobile, DOB, status only |
| DELETE | `/{id}` | ADMIN | Soft-disable |

### Employments — `/api/employments`

| Method | Path | Roles |
|--------|------|-------|
| GET | `/`, `/{id}` | ADMIN, MANAGER |
| POST | `/` | ADMIN, MANAGER |
| PUT | `/{id}` | ADMIN, MANAGER |

### Deduction rates — `/api/deduction-rates`

| Method | Path | Roles |
|--------|------|-------|
| GET, POST, PUT, DELETE | various | ADMIN only |

### Payroll — `/api/payroll`

| Method | Path | Roles |
|--------|------|-------|
| POST | `/generate` | MANAGER |
| GET | `/`, `/{id}`, `/month/{m}/year/{y}` | ADMIN, MANAGER |
| GET | `/my-payslips` | EMPLOYEE, ADMIN, MANAGER |
| PUT | `/{id}/approve` | ADMIN |
| GET | `/messages` | ADMIN |
| GET | `/messages/employee/{employeeId}` | ADMIN, MANAGER |

Full JSON examples: [TESTING-GUIDE.md](TESTING-GUIDE.md) and [README.md](README.md).

---

## 10. Typical business workflow

```
1. Register users (Admin once, then Manager, Employees)
2. Login as Manager or Admin
3. Create Employment for each employee (base salary required for payroll)
4. (Optional) Seed or CRUD deduction_rate rows as Admin
5. Login as Manager → POST /api/payroll/generate { month, year }
6. Login as Admin → PUT /api/payroll/{id}/approve for each payslip
7. GET /api/payroll/messages (Admin) or employee views GET /api/payroll/my-payslips
```

---

## 11. Configuration (`application.properties`)

| Property | Meaning |
|----------|---------|
| `server.port` | Default `8080` |
| `spring.datasource.*` | PostgreSQL connection (`erp_payroll_db`) |
| `spring.jpa.hibernate.ddl-auto` | `update` — auto schema sync |
| `jwt.secret` | HMAC key for JWT signing |
| `jwt.expiration` | Token lifetime in ms (default 3600000 = 1 hour) |
| `springdoc.*` | Swagger UI at `/swagger-ui.html` |

Change database credentials in this file for your environment; do not commit production secrets.

---

## 12. Running and exploring the code

```bash
# Ensure PostgreSQL is running and database exists
# Update src/main/resources/application.properties if needed

mvn spring-boot:run
```

- **Swagger UI:** http://localhost:8080/swagger-ui.html  
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs  

Optional SQL after first run:

```bash
psql -U postgres -d erp_payroll_db -f src/main/resources/init-deduction-rates.sql
```

If port 8080 is in use, stop the other process or change `server.port`.

---

## 13. Design decisions and limitations

| Topic | Decision |
|-------|----------|
| Single admin | Enforced at registration in `AuthService` |
| Employee = user | No separate `users` table; `Employee` holds credentials |
| Payroll rates | Hardcoded in `PayrollService`; `deduction_rate` table is parallel config |
| Stateless API | No server sessions; JWT only |
| Soft deletes | Employees disabled, deduction rates deactivated |
| No email worker | Messages stored only; `sentStatus` not updated by a mailer |
| Password in API responses | JPA may serialize `Employee.password` on GET unless you add `@JsonIgnore` (consider for production) |

---

## 14. Extending the codebase

Common extension points:

1. **Read rates from DB in payroll** — Inject `DeductionRateService` into `PayrollService` and replace constants.
2. **Email notifications** — Scheduled job or listener that reads `PayrollMessage` where `sentStatus = PENDING`.
3. **Separate DTOs for responses** — Hide password hash and internal fields from JSON.
4. **Audit logging** — Aspect or listener on approve/generate actions.
5. **Integration tests** — `@SpringBootTest` + Testcontainers for PostgreSQL.

---

## 15. File index (all Java sources)

| File | Package |
|------|---------|
| `ErpPayrollApplication.java` | root |
| `AuthController.java` | controller |
| `EmployeeController.java` | controller |
| `EmploymentController.java` | controller |
| `DeductionRateController.java` | controller |
| `PayrollController.java` | controller |
| `AuthService.java` | service |
| `EmployeeService.java` | service |
| `EmploymentService.java` | service |
| `DeductionRateService.java` | service |
| `PayrollService.java` | service |
| `EmployeeRepository.java` | repository |
| `EmploymentRepository.java` | repository |
| `DeductionRateRepository.java` | repository |
| `PayrollDeductionRepository.java` | repository |
| `PayrollMessageRepository.java` | repository |
| `Employee.java` | entity |
| `Employment.java` | entity |
| `DeductionRate.java` | entity |
| `PayrollDeduction.java` | entity |
| `PayrollMessage.java` | entity |
| `LoginRequest.java` … `GeneratePayrollRequest.java` | dto.request |
| `ApiResponse.java`, `AuthResponse.java` | dto.response |
| `SecurityConfig.java`, `SwaggerConfig.java` | config |
| `JwtTokenProvider.java`, `JwtAuthenticationFilter.java`, `UserDetailsImpl.java`, `UserDetailsServiceImpl.java` | security |
| `GlobalExceptionHandler.java` | exception |

---

*Document version: 1.0 — matches codebase as of ERP Payroll 1.0.0 (Spring Boot 3.2.0, PostgreSQL).*
