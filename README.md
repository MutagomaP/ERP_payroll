# ERP Payroll Management System

Enterprise Resource Planning (ERP) - Payroll and Employee Management System for Rwanda Government

## 📋 Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Database Schema](#database-schema)
- [Setup Instructions](#setup-instructions)
- [API Documentation](#api-documentation)
- [Security & Authentication](#security--authentication)
- [Payroll Calculation](#payroll-calculation)
- [Testing with Postman](#testing-with-postman)

## 🎯 Overview

This system manages employee information and payroll processing for Rwanda Government institutions. It includes:
- Employee Management with role-based access control
- Employment records management
- Configurable tax and deduction rates
- Automated payroll generation and calculation
- Payroll approval workflow
- Automated notification system

## ✨ Features

### Task 1: Database Design (Spring Data JPA)
- ✅ Employee table with authentication details
- ✅ Employment table with job details
- ✅ Deduction rates table for tax percentages
- ✅ Payroll deduction table with salary calculations
- ✅ Payroll message table for notifications

### Task 2: Employee & Employment Management
- ✅ CRUD operations for employees
- ✅ CRUD operations for employment records
- ✅ JWT-based authentication and authorization
- ✅ Role-based access control (ADMIN, MANAGER, EMPLOYEE)

### Task 3: Deductions & Taxes Management
- ✅ Configurable deduction rates
- ✅ Default rates: Employee Tax (30%), Pension (6%), Medical (5%), Cash Advance (5%), Sinking Fund (1%), Transport (1%)
- ✅ Housing and Transport allowances (14% each)

### Task 4: Payroll Generation
- ✅ Automated payroll calculation
- ✅ Gross salary = Base + Housing (14%) + Transport (14%)
- ✅ Net salary = Gross - (Tax + Pension + Medical + Other deductions)
- ✅ Duplicate prevention (unique constraint on employee/month/year)
- ✅ Only active employees included

### Task 5: Message Generation & Email Notification
- ✅ Automatic message generation on payroll approval
- ✅ Message format: "Dear [Name], Your salary of [Month/Year] from Rwanda Government RWF [Amount] has been credited to your [Code] account successfully."
- ✅ Database-level message storage

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + JWT
- **API Documentation**: Swagger/OpenAPI 3.0
- **Build Tool**: Maven
- **Password Encryption**: BCrypt

## 🏗 System Architecture

```
┌─────────────┐
│   Client    │
│ (Postman/   │
│  Swagger)   │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────────┐
│   JWT Authentication Filter     │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│      Controller Layer           │
│  (REST API Endpoints)           │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│      Service Layer              │
│  (Business Logic)               │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│    Repository Layer             │
│  (Spring Data JPA)              │
└──────┬──────────────────────────┘
       │
       ▼
┌─────────────────────────────────┐
│      MySQL Database             │
└─────────────────────────────────┘
```

## 📊 Database Schema

### Entity Relationship Diagram

```
EMPLOYEE (1) ──────< (1) EMPLOYMENT
                           │
                           │
                           ▼
                    PAYROLL_DEDUCTION (*)
                           │
                           │
                           ▼
                    PAYROLL_MESSAGE (1)

DEDUCTION_RATE (Configuration Table)
```

### Tables

1. **employee**: User authentication and personal info
2. **employment**: Job details and base salary
3. **deduction_rate**: Tax and deduction percentages
4. **payroll_deduction**: Monthly salary calculations
5. **payroll_message**: Notification messages

## 🚀 Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Postman (for API testing)

### Step 1: Database Setup

```sql
CREATE DATABASE erp_payroll_db;
```

### Step 2: Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/erp_payroll_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Step 3: Build the Project

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Step 5: Access Swagger UI

Open browser: `http://localhost:8080/swagger-ui.html`

## 📚 API Documentation

### Authentication APIs

#### 1. Register Employee
```
POST /api/auth/register
Content-Type: application/json

{
  "code": "EMP001",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@gov.rw",
  "password": "password123",
  "roles": "ROLE_ADMIN",
  "mobile": "+250788123456",
  "dateOfBirth": "1990-01-15"
}
```

#### 2. Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@gov.rw",
  "password": "password123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "email": "john.doe@gov.rw",
    "roles": "ROLE_ADMIN"
  }
}
```

### Employee Management APIs (ROLE_ADMIN, ROLE_MANAGER)

```
GET    /api/employees              - Get all employees
GET    /api/employees/{id}         - Get employee by ID
GET    /api/employees/me           - Get current employee
PUT    /api/employees/{id}         - Update employee
DELETE /api/employees/{id}         - Disable employee
```

### Employment Management APIs (ROLE_ADMIN, ROLE_MANAGER)

```
GET    /api/employments            - Get all employments
GET    /api/employments/{id}       - Get employment by ID
POST   /api/employments            - Create employment
PUT    /api/employments/{id}       - Update employment
```

**Create Employment Example:**
```json
{
  "code": "EMP-001-2025",
  "employeeId": 1,
  "department": "IT Department",
  "position": "Software Developer",
  "baseSalary": 70000,
  "joiningDate": "2025-01-01"
}
```

### Deduction Rate APIs (ROLE_ADMIN)

```
GET    /api/deduction-rates        - Get all deduction rates
GET    /api/deduction-rates/{id}   - Get deduction rate by ID
POST   /api/deduction-rates        - Create deduction rate
PUT    /api/deduction-rates/{id}   - Update deduction rate
DELETE /api/deduction-rates/{id}   - Deactivate deduction rate
```

**Create Deduction Rate Example:**
```json
{
  "code": "TAX-001",
  "deductionName": "Employee Tax",
  "percentage": 30
}
```

### Payroll Management APIs

```
POST   /api/payroll/generate                    - Generate payroll (ROLE_MANAGER)
GET    /api/payroll                             - Get all payrolls (ROLE_ADMIN, ROLE_MANAGER)
GET    /api/payroll/{id}                        - Get payroll by ID
GET    /api/payroll/month/{month}/year/{year}  - Get payroll by month/year
GET    /api/payroll/my-payslips                 - Get my payslips (All roles)
PUT    /api/payroll/{id}/approve                - Approve payroll (ROLE_ADMIN)
GET    /api/payroll/messages                    - Get all messages (ROLE_ADMIN)
```

**Generate Payroll Example:**
```json
{
  "month": 6,
  "year": 2025
}
```

## 🔐 Security & Authentication

### Roles and Permissions

| Role | Permissions |
|------|-------------|
| **ROLE_ADMIN** | - Approve payroll<br>- Manage deduction rates<br>- View all data<br>- View own payslips |
| **ROLE_MANAGER** | - Generate payroll<br>- Manage employees<br>- Manage employments<br>- View payroll data |
| **ROLE_EMPLOYEE** | - View own details<br>- View own payslips<br>- Download pay slip |

### JWT Token Usage

After login, include the JWT token in all API requests:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Token expires after 1 hour (3600000 ms).

## 💰 Payroll Calculation

### Formula

```
Housing Allowance = Base Salary × 14%
Transport Allowance = Base Salary × 14%
Gross Salary = Base Salary + Housing + Transport

Employee Tax = Base Salary × 30%
Pension = Base Salary × 6%
Medical Insurance = Base Salary × 5%
Other Deductions = Base Salary × 6% (Cash Advance 5% + Sinking Fund 1%)

Net Salary = Gross Salary - (Tax + Pension + Medical + Other)
```

### Example Calculation (Base Salary: 70,000 RWF)

```
Housing:    70,000 × 14% = 9,800
Transport:  70,000 × 14% = 9,800
Gross:      70,000 + 9,800 + 9,800 = 89,600

Tax:        70,000 × 30% = 21,000
Pension:    70,000 × 6%  = 4,200
Medical:    70,000 × 5%  = 3,500
Other:      70,000 × 6%  = 4,200

Net Salary: 89,600 - (21,000 + 4,200 + 3,500 + 4,200) = 57,400 RWF
```

## 🧪 Testing with Postman

### Step-by-Step Testing Guide

1. **Register Admin User**
   - POST `/api/auth/register` with ROLE_ADMIN

2. **Login**
   - POST `/api/auth/login`
   - Copy the JWT token from response

3. **Set Authorization Header**
   - In Postman, go to Authorization tab
   - Select "Bearer Token"
   - Paste the JWT token

4. **Create Employment Records**
   - POST `/api/employments` for each employee

5. **Generate Payroll**
   - POST `/api/payroll/generate` with month and year

6. **View Generated Payroll**
   - GET `/api/payroll/month/{month}/year/{year}`

7. **Approve Payroll (as ADMIN)**
   - PUT `/api/payroll/{id}/approve`

8. **View Messages**
   - GET `/api/payroll/messages`

## 📝 Sample Data for Testing

### 1. Register Admin
```json
{
  "code": "ADM001",
  "firstName": "Admin",
  "lastName": "User",
  "email": "admin@gov.rw",
  "password": "admin123",
  "roles": "ROLE_ADMIN",
  "mobile": "+250788111111",
  "dateOfBirth": "1985-01-01"
}
```

### 2. Register Manager
```json
{
  "code": "MGR001",
  "firstName": "Manager",
  "lastName": "User",
  "email": "manager@gov.rw",
  "password": "manager123",
  "roles": "ROLE_MANAGER",
  "mobile": "+250788222222",
  "dateOfBirth": "1988-05-15"
}
```

### 3. Register Employees
```json
{
  "code": "EMP123",
  "firstName": "Mugabo",
  "lastName": "Peter",
  "email": "mugabo@gov.rw",
  "password": "emp123",
  "roles": "ROLE_EMPLOYEE",
  "mobile": "+250788333333",
  "dateOfBirth": "1992-03-20"
}
```

```json
{
  "code": "EMP224",
  "firstName": "Iratire",
  "lastName": "Jean",
  "email": "iratire@gov.rw",
  "password": "emp224",
  "roles": "ROLE_EMPLOYEE",
  "mobile": "+250788444444",
  "dateOfBirth": "1995-07-10"
}
```

### 4. Create Employment Records

For Mugabo (Base: 70,000):
```json
{
  "code": "EMP-123-2025",
  "employeeId": 3,
  "department": "Finance",
  "position": "Accountant",
  "baseSalary": 70000,
  "joiningDate": "2024-01-01"
}
```

For Iratire (Base: 35,000):
```json
{
  "code": "EMP-224-2025",
  "employeeId": 4,
  "department": "HR",
  "position": "HR Assistant",
  "baseSalary": 35000,
  "joiningDate": "2024-06-01"
}
```

## 🔍 Key Features Implementation

### ✅ Duplicate Prevention
- Unique constraint on `(employment_id, month, year)` in payroll_deduction table
- Validation in service layer before creating payroll

### ✅ Active Employee Validation
- Only employees with status = ACTIVE are included in payroll
- Only employments with status = ACTIVE are processed

### ✅ Deduction Validation
- System validates that total deductions don't exceed gross salary
- Throws exception if validation fails

### ✅ Message Generation
- Automatic message creation when payroll is approved
- Message stored in database with PENDING status
- Can be integrated with email service for actual sending

## 📞 Support

For issues or questions, contact:
- Email: info@gov.rw
- Developer: Rwanda Government IT Department

## 📄 License

Copyright © 2025 Rwanda Government. All rights reserved.

---

**Note**: This system is designed for the Rwanda Government ERP practical exam. All calculations follow the specifications provided in the exam requirements.
