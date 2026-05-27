# ERP Payroll System - Quick Start Guide

## 🚀 5-Minute Setup

### Step 1: Database Setup (1 minute)
```sql
CREATE DATABASE erp_payroll_db;
```

### Step 2: Configure Database (30 seconds)
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Step 3: Run Application (1 minute)
```bash
mvn spring-boot:run
```

Wait for: `ERP Payroll Management System Started!`

### Step 4: Open Swagger UI (30 seconds)
Open browser: `http://localhost:8080/swagger-ui.html`

---

## 🧪 Quick Test (2 minutes)

### 1. Register Admin
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "code": "ADM001",
    "firstName": "Admin",
    "lastName": "User",
    "email": "admin@gov.rw",
    "password": "admin123",
    "roles": "ROLE_ADMIN",
    "mobile": "+250788111111",
    "dateOfBirth": "1985-01-01"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@gov.rw",
    "password": "admin123"
  }'
```

**Copy the token from response!**

### 3. Test Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## 📝 Complete Testing

For complete testing with sample data, see **TESTING-GUIDE.md**

---

## 📚 Documentation

- **README.md** - Full documentation
- **DESIGN.md** - Architecture and design
- **TESTING-GUIDE.md** - Complete testing instructions
- **PROJECT-SUMMARY.md** - Project overview
- **Swagger UI** - http://localhost:8080/swagger-ui.html

---

## 🎯 Key Features

✅ JWT Authentication  
✅ Role-Based Access Control (ADMIN, MANAGER, EMPLOYEE)  
✅ Automated Payroll Calculation  
✅ Duplicate Prevention  
✅ Message Generation on Approval  
✅ Swagger API Documentation  

---

## 💡 Sample Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@gov.rw | admin123 |
| Manager | manager@gov.rw | manager123 |
| Employee | mugabo@gov.rw | emp123 |

*(Register these users first using the registration endpoint)*

---

## 🔧 Troubleshooting

**Database Connection Error?**
- Check MySQL is running
- Verify credentials in application.properties

**Port 8080 Already in Use?**
- Change port in application.properties: `server.port=8081`

**JWT Token Expired?**
- Login again to get a new token
- Tokens expire after 1 hour

---

## 📞 Need Help?

See detailed guides:
- Setup issues → README.md
- Testing → TESTING-GUIDE.md
- Architecture → DESIGN.md
- Overview → PROJECT-SUMMARY.md

---

**Ready to start? Run `mvn spring-boot:run` and open Swagger UI!** 🚀
