# MakotoPay
Digital Wallet &amp; Payment Processing  System built with Spring Boot 4.x, JWT, Redis, MySQL
# 💳 MakoToPay — Digital Wallet & Payment Processing System

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen)
![Java](https://img.shields.io/badge/Java-21-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Redis](https://img.shields.io/badge/Redis-3.0-red)
![JWT](https://img.shields.io/badge/JWT-Security-yellow)
![Swagger](https://img.shields.io/badge/Swagger-API%20Docs-green)

---

## 🚀 About The Project

**MakoToPay** is a production-grade Digital Wallet & Payment 
Processing System that solves real-world payment problems 
like double payments, fraud detection, and rate limiting —
the same problems solved by PhonePe, GPay and Razorpay.

> Built with Java 21 + Spring Boot 4.x + Redis + MySQL

---

## 💡 Real Problems Solved

| Problem | Solution |
|---|---|
| Double Payment | Redis Idempotency Keys |
| Concurrent Transactions | Optimistic Locking (@Version) |
| Bot Attacks / Fraud | Redis Rate Limiting |
| Unauthorized Access | JWT + Spring Security |
| Transaction Failures | @Transactional Rollback |
| Password Security | BCrypt Encoding |

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Programming Language |
| Spring Boot | 4.0.5 | Backend Framework |
| Spring Security | 4.x | Authentication & Authorization |
| MySQL | 8.0 | Primary Database |
| Redis | 3.0 | Caching & Rate Limiting |
| JWT | 0.11.5 | Token Based Auth |
| Swagger | 2.8.6 | API Documentation |
| Postman | Latest | API Testing |
| Lombok | Latest | Code Generation |
| Maven | Latest | Build Tool |

---

## 📋 Modules

### 🔐 Module 1 — Authentication
- User Registration with Wallet auto-creation
- JWT Token based Login
- BCrypt Password Encoding
- Role Based Access Control (USER / ADMIN)

### 👛 Module 2 — Wallet Management
- Check Wallet Balance
- Add Money to Wallet
- Transaction History with Pagination & Filter

### 💸 Module 3 — Money Transfer
- Transfer Money to any user
- Idempotency Protection (No Double Payments)
- @Transactional Rollback on failure
- Optimistic Locking for concurrent transfers

### 🚨 Module 4 — Fraud Detection
- Redis Rate Limiting (Max 5 transfers per 2 minutes)
- Automatic blocking on suspicious activity
- Fraud Log maintenance

### 👨‍💼 Module 5 — Admin Panel
- View all Users (Paginated)
- View all Transactions (Paginated)
- Block / Unblock Users
- Dashboard Statistics

---

## 🔗 API Endpoints

### Auth APIs
```
POST /api/auth/register    → Register new user
POST /api/auth/login       → Login & get JWT token
```

### Wallet APIs
```
GET  /api/wallet/balance          → Check balance
POST /api/wallet/add-money        → Add money
POST /api/wallet/transfer         → Transfer money
GET  /api/wallet/transactions     → Transaction history
```

### Admin APIs
```
GET  /api/admin/users                  → All users
GET  /api/admin/transactions           → All transactions
PUT  /api/admin/block-user/{id}        → Block user
PUT  /api/admin/unblock-user/{id}      → Unblock user
GET  /api/admin/dashboard/stats        → Dashboard stats
```

---

## ⚙️ Setup & Installation

### Prerequisites
```
✅ Java 21
✅ MySQL 8.0
✅ Redis 3.0
✅ Maven
```

### Step 1 — Clone Repository
```bash
git clone https://github.com/viswanath7095/MakotoPay.git
cd MakotoPay
```

### Step 2 — Database Setup
```sql
CREATE DATABASE makotopaydb;
```

### Step 3 — Configure application.properties
```bash
cp src/main/resources/application.properties.example 
   src/main/resources/application.properties
```
Update these values:
```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
jwt.secret=YOUR_JWT_SECRET
```

### Step 4 — Start Redis
```bash
redis-server
```

### Step 5 — Run Application
```bash
mvn spring-boot:run
```

### Step 6 — Access Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

---

## 📸 Swagger UI

All APIs are documented and testable via Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🎯 Key Concepts Implemented

```
✅ JWT Authentication — Stateless, Scalable
✅ BCrypt Password Hashing — Secure storage
✅ Redis Idempotency — No double payments
✅ Redis Rate Limiting — Fraud prevention
✅ Optimistic Locking — Concurrent transaction safety
✅ @Transactional — Atomic operations
✅ Global Exception Handling — Clean error responses
✅ DTO Pattern — Secure API responses
✅ Pagination & Filtering — Performance optimization
✅ Role Based Access Control — Admin vs User
```

---

## 👨‍💻 Author

**Viswanath**
- GitHub: [@viswanath7095](https://github.com/viswanath7095)
- Project: MakoToPay Digital Wallet System

---

## 📄 License

This project is open source and available under 
the [MIT License](LICENSE).
