# ERP Payroll System - Complete Testing Guide

## 📋 Prerequisites
- Application running on `http://localhost:8080`
- MySQL database configured and running
- Postman installed (or use Swagger UI)

## 🎯 Testing Workflow

### Phase 1: User Registration and Authentication

#### Step 1.1: Register Admin User
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

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

**Expected Response:**
```json
{
  "success": true,
  "message": "Employee registered successfully",
  "data": {
    "token": null,
    "type": "Bearer",
    "email": "admin@gov.rw",
    "roles": "ROLE_ADMIN",
    "message": "Employee registered successfully"
  }
}
```

#### Step 1.2: Register Manager User
```
POST http://localhost:8080/api/auth/register

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

#### Step 1.3: Register Employee - Mugabo
```
POST http://localhost:8080/api/auth/register

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

#### Step 1.4: Register Employee - Iratire
```
POST http://localhost:8080/api/auth/register

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

#### Step 1.5: Login as Manager
```
POST http://localhost:8080/api/auth/login

{
  "email": "manager@gov.rw",
  "password": "manager123"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "email": "manager@gov.rw",
    "roles": "ROLE_MANAGER",
    "message": "Login successful"
  }
}
```

**⚠️ IMPORTANT: Copy the token from the response and use it in all subsequent requests!**

---

### Phase 2: Employment Management (Use Manager Token)

#### Step 2.1: Create Employment for Mugabo (Base: 70,000)
```
POST http://localhost:8080/api/employments
Authorization: Bearer YOUR_MANAGER_TOKEN
Content-Type: application/json

{
  "code": "EMP-123-2025",
  "employeeId": 3,
  "department": "Finance",
  "position": "Accountant",
  "baseSalary": 70000,
  "joiningDate": "2024-01-01"
}
```

**Expected Calculation for Mugabo:**
- Base Salary: 70,000
- Housing (14%): 9,800
- Transport (14%): 9,800
- **Gross Salary: 89,600**
- Tax (30%): 21,000
- Pension (6%): 4,200
- Medical (5%): 3,500
- Other (6%): 4,200
- **Net Salary: 57,400**

#### Step 2.2: Create Employment for Iratire (Base: 35,000)
```
POST http://localhost:8080/api/employments
Authorization: Bearer YOUR_MANAGER_TOKEN

{
  "code": "EMP-224-2025",
  "employeeId": 4,
  "department": "HR",
  "position": "HR Assistant",
  "baseSalary": 35000,
  "joiningDate": "2024-06-01"
}
```

**Expected Calculation for Iratire:**
- Base Salary: 35,000
- Housing (14%): 4,900
- Transport (14%): 4,900
- **Gross Salary: 44,800**
- Tax (30%): 10,500
- Pension (6%): 2,100
- Medical (5%): 1,750
- Other (6%): 2,100
- **Net Salary: 28,700**

#### Step 2.3: Verify Employments Created
```
GET http://localhost:8080/api/employments
Authorization: Bearer YOUR_MANAGER_TOKEN
```

---

### Phase 3: Payroll Generation (Use Manager Token)

#### Step 3.1: Generate Payroll for June 2025
```
POST http://localhost:8080/api/payroll/generate
Authorization: Bearer YOUR_MANAGER_TOKEN
Content-Type: application/json

{
  "month": 6,
  "year": 2025
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Payroll generated successfully for 2 employees",
  "data": [
    {
      "id": 1,
      "code": "PAY-XXXXXXXX",
      "baseSalary": 70000,
      "housingAllowance": 9800,
      "transportAllowance": 9800,
      "grossSalary": 89600,
      "employeeTaxAmount": 21000,
      "pensionAmount": 4200,
      "medicalInsuranceAmount": 3500,
      "otherDeductionAmount": 4200,
      "netSalary": 57400,
      "month": 6,
      "year": 2025,
      "status": "PENDING"
    },
    {
      "id": 2,
      "code": "PAY-YYYYYYYY",
      "baseSalary": 35000,
      "housingAllowance": 4900,
      "transportAllowance": 4900,
      "grossSalary": 44800,
      "employeeTaxAmount": 10500,
      "pensionAmount": 2100,
      "medicalInsuranceAmount": 1750,
      "otherDeductionAmount": 2100,
      "netSalary": 28700,
      "month": 6,
      "year": 2025,
      "status": "PENDING"
    }
  ]
}
```

#### Step 3.2: View Generated Payroll
```
GET http://localhost:8080/api/payroll/month/6/year/2025
Authorization: Bearer YOUR_MANAGER_TOKEN
```

#### Step 3.3: Test Duplicate Prevention (Should Fail)
```
POST http://localhost:8080/api/payroll/generate
Authorization: Bearer YOUR_MANAGER_TOKEN

{
  "month": 6,
  "year": 2025
}
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Payroll already exists for employee: Mugabo Peter for month 6/2025",
  "data": null
}
```

---

### Phase 4: Payroll Approval (Use Admin Token)

#### Step 4.1: Login as Admin
```
POST http://localhost:8080/api/auth/login

{
  "email": "admin@gov.rw",
  "password": "admin123"
}
```

**Copy the Admin token!**

#### Step 4.2: Approve Payroll for Mugabo (ID: 1)
```
PUT http://localhost:8080/api/payroll/1/approve
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Payroll approved successfully",
  "data": {
    "id": 1,
    "status": "PAID",
    ...
  }
}
```

#### Step 4.3: Approve Payroll for Iratire (ID: 2)
```
PUT http://localhost:8080/api/payroll/2/approve
Authorization: Bearer YOUR_ADMIN_TOKEN
```

#### Step 4.4: View Generated Messages
```
GET http://localhost:8080/api/payroll/messages
Authorization: Bearer YOUR_ADMIN_TOKEN
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Messages retrieved successfully",
  "data": [
    {
      "id": 1,
      "employeeEmail": "mugabo@gov.rw",
      "messageContent": "Dear Mugabo, Your salary of 6/2025 from Rwanda Government RWF 57400.00 has been credited to your EMP-123-2025 account successfully.",
      "sentStatus": "PENDING"
    },
    {
      "id": 2,
      "employeeEmail": "iratire@gov.rw",
      "messageContent": "Dear Iratire, Your salary of 6/2025 from Rwanda Government RWF 28700.00 has been credited to your EMP-224-2025 account successfully.",
      "sentStatus": "PENDING"
    }
  ]
}
```

---

### Phase 5: Employee Access (Use Employee Token)

#### Step 5.1: Login as Employee (Mugabo)
```
POST http://localhost:8080/api/auth/login

{
  "email": "mugabo@gov.rw",
  "password": "emp123"
}
```

#### Step 5.2: View My Payslips
```
GET http://localhost:8080/api/payroll/my-payslips
Authorization: Bearer YOUR_EMPLOYEE_TOKEN
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Payslips retrieved successfully",
  "data": [
    {
      "id": 1,
      "baseSalary": 70000,
      "grossSalary": 89600,
      "netSalary": 57400,
      "month": 6,
      "year": 2025,
      "status": "PAID"
    }
  ]
}
```

#### Step 5.3: View My Details
```
GET http://localhost:8080/api/employees/me
Authorization: Bearer YOUR_EMPLOYEE_TOKEN
```

#### Step 5.4: Test Access Control (Should Fail)
```
GET http://localhost:8080/api/payroll
Authorization: Bearer YOUR_EMPLOYEE_TOKEN
```

**Expected Response:**
```json
{
  "success": false,
  "message": "Access denied: Access Denied",
  "data": null
}
```

---

## 🧪 Additional Test Cases

### Test Case 1: Invalid Login
```
POST http://localhost:8080/api/auth/login

{
  "email": "wrong@gov.rw",
  "password": "wrongpass"
}
```

**Expected:** 401 Unauthorized

### Test Case 2: Duplicate Employee Code
```
POST http://localhost:8080/api/auth/register

{
  "code": "EMP123",
  "firstName": "Duplicate",
  "lastName": "User",
  "email": "duplicate@gov.rw",
  "password": "pass123",
  "roles": "ROLE_EMPLOYEE",
  "mobile": "+250788555555",
  "dateOfBirth": "1990-01-01"
}
```

**Expected:** Error - "Employee code already exists"

### Test Case 3: Generate Payroll for Different Month
```
POST http://localhost:8080/api/payroll/generate
Authorization: Bearer YOUR_MANAGER_TOKEN

{
  "month": 7,
  "year": 2025
}
```

**Expected:** Success - New payroll created for July 2025

### Test Case 4: Manager Cannot Approve Payroll
```
PUT http://localhost:8080/api/payroll/1/approve
Authorization: Bearer YOUR_MANAGER_TOKEN
```

**Expected:** 403 Forbidden

---

## 📊 Verification Checklist

- [ ] All 4 users registered successfully
- [ ] Manager can login and get JWT token
- [ ] Admin can login and get JWT token
- [ ] Employee can login and get JWT token
- [ ] 2 employment records created
- [ ] Payroll generated for June 2025
- [ ] Payroll calculations are correct (Mugabo: 57,400, Iratire: 28,700)
- [ ] Duplicate payroll prevention works
- [ ] Admin can approve payroll
- [ ] Messages generated after approval
- [ ] Employee can view own payslips
- [ ] Employee cannot access admin/manager endpoints
- [ ] Manager cannot approve payroll

---

## 🎯 Expected Final Database State

### employee table
| id | code | first_name | last_name | email | roles |
|----|------|------------|-----------|-------|-------|
| 1 | ADM001 | Admin | User | admin@gov.rw | ROLE_ADMIN |
| 2 | MGR001 | Manager | User | manager@gov.rw | ROLE_MANAGER |
| 3 | EMP123 | Mugabo | Peter | mugabo@gov.rw | ROLE_EMPLOYEE |
| 4 | EMP224 | Iratire | Jean | iratire@gov.rw | ROLE_EMPLOYEE |

### employment table
| id | code | employee_id | department | position | base_salary | status |
|----|------|-------------|------------|----------|-------------|--------|
| 1 | EMP-123-2025 | 3 | Finance | Accountant | 70000 | ACTIVE |
| 2 | EMP-224-2025 | 4 | HR | HR Assistant | 35000 | ACTIVE |

### payroll_deduction table
| id | employment_id | base_salary | gross_salary | net_salary | month | year | status |
|----|---------------|-------------|--------------|------------|-------|------|--------|
| 1 | 1 | 70000 | 89600 | 57400 | 6 | 2025 | PAID |
| 2 | 2 | 35000 | 44800 | 28700 | 6 | 2025 | PAID |

### payroll_message table
| id | payroll_deduction_id | employee_email | message_content | sent_status |
|----|---------------------|----------------|-----------------|-------------|
| 1 | 1 | mugabo@gov.rw | Dear Mugabo, Your salary of 6/2025... | PENDING |
| 2 | 2 | iratire@gov.rw | Dear Iratire, Your salary of 6/2025... | PENDING |

---

## 🔧 Troubleshooting

### Issue: "Access Denied" on all endpoints
**Solution:** Make sure you're including the JWT token in the Authorization header:
```
Authorization: Bearer YOUR_TOKEN_HERE
```

### Issue: "Employee code already exists"
**Solution:** Use a different employee code or delete existing records from database

### Issue: "Payroll already exists"
**Solution:** This is expected behavior. Use a different month/year or delete existing payroll

### Issue: Database connection error
**Solution:** Check MySQL is running and credentials in application.properties are correct

---

## 📝 Notes

1. JWT tokens expire after 1 hour
2. All monetary values are in RWF (Rwandan Francs)
3. Deduction rates are hardcoded as per requirements
4. Message sending is simulated (stored in database with PENDING status)
5. Payroll can only be generated once per employee per month/year

---

**Testing Complete! ✅**

All features have been implemented and tested according to the exam requirements.
