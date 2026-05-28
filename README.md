# RupeeRoute 💸

A microservices-based expense splitting application built with Spring Boot, Kafka, PostgreSQL, Redis, and Docker.

---

## Problem it Solves

Group trips, hostel roommates, office lunches — expense splitting is painful manually. RupeeRoute tracks who paid what, auto-calculates minimum transactions to settle debts using a greedy graph algorithm, and publishes real-time events via Kafka.

---

## Architecture

```
Client
  │
  ▼
API Gateway (8080)  ──── JWT Validation
  │
  ├──► expense-service (8081)   ──── PostgreSQL
  │         │
  │         └──► Kafka ──► settlement-service (8082) ──── PostgreSQL + Redis
  │                    └──► notification-service (8083)
  │
  └──► settlement-service (8082)
```

---

## Services

| Service | Port | Responsibility |
|---------|------|----------------|
| api-gateway | 8080 | JWT validation, routing |
| expense-service | 8081 | Users, groups, expenses, auth |
| settlement-service | 8082 | Debt calculation, settlements |
| notification-service | 8083 | Expense notifications |

---

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.5.14 | Microservices framework |
| Spring Cloud Gateway | 2025.0.2 | API Gateway |
| Apache Kafka | 3.7 | Event streaming |
| PostgreSQL | 16 | Primary database |
| Redis | 7 | Idempotency store |
| Docker Compose | - | Container orchestration |
| JWT (jjwt) | 0.12.3 | Authentication |
| Flyway | - | Database migrations |

---

## Request Flow

```
1. User adds expense via POST /api/expenses
2. expense-service saves to PostgreSQL
3. Kafka event published → rupeeroute.expense.added.v1
4. settlement-service consumes event → recalculates debt graph
5. notification-service consumes event → logs notification
6. GET /api/settlements returns minimum transactions
```

---

## Settlement Algorithm

Uses greedy graph simplification to minimize number of transactions:

- Converts N*(N-1) potential transactions to at most N-1
- Uses max-heap for creditors, min-heap for debtors
- Amounts stored as paise (long) to avoid BigDecimal comparison issues

---

## Prerequisites

- Java 21
- Maven 3.8+
- Docker Desktop

---

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/your-username/rupeeroute.git
cd rupeeroute
```

### 2. Build all services

```bash
cd expense-service && mvn clean package -DskipTests && cd ..
cd settlement-service && mvn clean package -DskipTests && cd ..
cd notification-service && mvn clean package -DskipTests && cd ..
cd api-gateway && mvn clean package -DskipTests && cd ..
```

### 3. Start everything

```bash
docker compose up --build
```

### 4. Verify all containers are running

```bash
docker ps
```

You should see 8 containers:
- rr-api-gateway
- rr-expense-service
- rr-settlement-service
- rr-notification-service
- rr-kafka
- rr-postgres
- rr-redis
- rr-zookeeper

---

## API Reference

All requests go through the API Gateway on port **8080**.

### Auth

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/register | Register new user | No |
| POST | /api/auth/login | Login, get JWT token | No |

### Users

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/users | Create user | No |
| GET | /api/users/{id} | Get user by ID | Yes |

### Groups

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/groups | Create group | Yes |
| GET | /api/groups/{id} | Get group | Yes |
| POST | /api/groups/{groupId}/members/{userId} | Add member | Yes |

### Expenses

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/expenses | Add expense | Yes |
| GET | /api/expenses/group/{groupId} | Get group expenses | Yes |
| GET | /api/expenses/group/{groupId}/balances | Get balances | Yes |

### Settlements

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | /api/settlements/group/{groupId} | Get all settlements | Yes |
| GET | /api/settlements/group/{groupId}/pending | Get pending settlements | Yes |

---

## Example Usage

### Register & Login

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Rahul","email":"rahul@test.com","password":"pass123"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"rahul@test.com","password":"pass123"}'
```

### Create Group & Add Expense

```bash
# Create group (use token from login)
curl -X POST http://localhost:8080/api/groups \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Goa Trip","createdBy":"USER_UUID"}'

# Add expense
curl -X POST http://localhost:8080/api/expenses \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "groupId": "GROUP_UUID",
    "paidBy": "USER_UUID",
    "amount": 900.00,
    "description": "Dinner",
    "splitType": "EQUAL"
  }'
```

---

## Kafka Topics

| Topic | Producer | Consumer(s) | Purpose |
|-------|----------|-------------|---------|
| rupeeroute.expense.added.v1 | expense-service | settlement-service, notification-service | Expense added event |
| rupeeroute.payment.settled.v1 | settlement-service | notification-service | Payment settled event |

---

## Database Schema

```sql
users           — id, name, email, password, upi_id, phone
groups          — id, name, created_by, created_at
group_members   — group_id, user_id, joined_at
expenses        — id, group_id, paid_by, amount, description, split_type
expense_splits  — id, expense_id, user_id, amount, settled
settlements     — id, group_id, from_user, to_user, amount, status
```

---

## Project Structure

```
rupeeroute/
├── api-gateway/
├── expense-service/
├── settlement-service/
├── notification-service/
├── docker-compose.yml
└── README.md
```

---

## Running Tests

```bash
# Run all tests
mvn test

# Run specific service tests
cd expense-service
mvn test
```

---

## Stopping the Application

```bash
docker compose down
```

To also remove volumes (clears database):

```bash
docker compose down -v
```

---

## Future Improvements

- Email/SMS notifications via Firebase FCM
- UPI payment integration
- Frontend (React/Flutter)
- Kubernetes deployment
- Rate limiting at API Gateway
- Service discovery with Eureka
