# Enterprise Digital Wallet Platform

Enterprise Digital Wallet Platform is a production-style backend application built using Spring Boot and PostgreSQL.

The project simulates a real-world digital wallet/payment system with support for:

- User onboarding
- Wallet management
- Deposits and withdrawals
- Peer-to-peer money transfers
- Transaction history tracking
- Validation and exception handling
- Dockerized local development

The primary goal of this project is to demonstrate enterprise backend engineering concepts using modern Java backend technologies and production-oriented architecture patterns.

---

# Tech Stack

## Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate ORM
- Maven

## Database

- PostgreSQL

## Infrastructure

- Docker
- Docker Compose

## Utilities

- Lombok
- Bean Validation
- OpenAPI / Swagger

---

# Planned Enterprise Features

The project roadmap includes:

- Kafka event-driven communication
- Redis caching
- Keycloak OAuth2 authentication
- Elasticsearch transaction search
- Netflix Conductor workflows
- Jenkins CI/CD pipeline
- Kubernetes deployment
- Prometheus monitoring
- Grafana dashboards
- Distributed tracing
- Rate limiting
- Audit logging

---

# Current Features

## User Module

- Create user
- Fetch user by ID
- Fetch all users

## Wallet Module

- Automatic wallet creation
- Fetch wallet by user ID
- Deposit money
- Withdraw money
- Balance validation

## Transaction Module

- Transfer money between users
- Transaction history by user
- Fetch transaction by ID
- Unique transaction reference numbers
- Transaction status tracking
- Transaction type tracking

## Platform Features

- Global exception handling
- Request validation
- DTO-based architecture
- Layered service architecture
- PostgreSQL persistence
- Dockerized development setup

---

# System Architecture

```mermaid
flowchart TD

    Client[Client / Postman / Swagger UI]

    Client --> ControllerLayer[REST Controller Layer]

    ControllerLayer --> UserService[User Service]
    ControllerLayer --> WalletService[Wallet Service]
    ControllerLayer --> TransactionService[Transaction Service]

    UserService --> UserRepository[User Repository]
    WalletService --> WalletRepository[Wallet Repository]
    TransactionService --> TransactionRepository[Transaction Repository]

    UserRepository --> PostgreSQL[(PostgreSQL)]
    WalletRepository --> PostgreSQL
    TransactionRepository --> PostgreSQL

    UserService --> WalletService
    WalletService --> TransactionRepository
    TransactionService --> WalletRepository
```

---

# High-Level Transaction Flow

```mermaid
sequenceDiagram

    participant Client
    participant TransactionController
    participant TransactionService
    participant WalletRepository
    participant TransactionRepository
    participant PostgreSQL

    Client->>TransactionController: Transfer Request

    TransactionController->>TransactionService: Validate Request

    TransactionService->>WalletRepository: Load Sender Wallet
    TransactionService->>WalletRepository: Load Receiver Wallet

    TransactionService->>WalletRepository: Debit Sender
    TransactionService->>WalletRepository: Credit Receiver

    TransactionService->>TransactionRepository: Save Transaction

    WalletRepository->>PostgreSQL: Persist Wallet Updates
    TransactionRepository->>PostgreSQL: Persist Transaction

    TransactionService-->>TransactionController: Transaction Response
    TransactionController-->>Client: Success Response
```

---

# Project Structure

```text
src/main/java/com/example/enterprise_digital_wallet
│
├── config
│
├── controller
│   ├── HealthController
│   ├── UserController
│   ├── WalletController
│   └── TransactionController
│
├── dto
│
├── entity
│
├── exception
│
├── repository
│
├── service
│
└── EnterpriseDigitalWalletApplication
```

---

# Database Design

## Users Table

```text
users
├── id
├── full_name
├── email
├── phone_number
├── created_at
```

## Wallets Table

```text
wallets
├── id
├── user_id
├── balance
├── currency
├── created_at
```

## Wallet Transactions Table

```text
wallet_transactions
├── id
├── sender_wallet_id
├── receiver_wallet_id
├── amount
├── currency
├── transaction_type
├── status
├── reference_number
├── created_at
```

---

# REST API Overview

# Health APIs

## Check Application Health

```http
GET /api/v1/health
```

---

# User APIs

## Create User

```http
POST /api/v1/users
```

## Get All Users

```http
GET /api/v1/users
```

## Get User By ID

```http
GET /api/v1/users/{userId}
```

---

# Wallet APIs

## Get Wallet By User ID

```http
GET /api/v1/wallets/users/{userId}
```

## Deposit Money

```http
POST /api/v1/wallets/users/{userId}/deposit
```

## Withdraw Money

```http
POST /api/v1/wallets/users/{userId}/withdraw
```

---

# Transaction APIs

## Transfer Money

```http
POST /api/v1/transactions/transfer
```

## Get Transaction History By User

```http
GET /api/v1/transactions/users/{userId}
```

## Get Transaction By ID

```http
GET /api/v1/transactions/{transactionId}
```

---

# Example API Requests

# Create User

```bash
curl -X POST "http://localhost:8080/api/v1/users" \
  -H "Content-Type: application/json" \
  -d '{
        "fullName":"Siddhant Sharma",
        "email":"siddhant@example.com",
        "phoneNumber":"+491234567890"
      }'
```

---

# Deposit Money

```bash
curl -X POST "http://localhost:8080/api/v1/wallets/users/{userId}/deposit" \
  -H "Content-Type: application/json" \
  -d '{
        "amount":100.00
      }'
```

---

# Withdraw Money

```bash
curl -X POST "http://localhost:8080/api/v1/wallets/users/{userId}/withdraw" \
  -H "Content-Type: application/json" \
  -d '{
        "amount":50.00
      }'
```

---

# Transfer Money

```bash
curl -X POST "http://localhost:8080/api/v1/transactions/transfer" \
  -H "Content-Type: application/json" \
  -d '{
        "senderUserId":"SENDER_USER_ID",
        "receiverUserId":"RECEIVER_USER_ID",
        "amount":150.00
      }'
```

---

# Local Development Setup

# 1. Clone Repository

```bash
git clone <repository-url>
cd enterprise-digital-wallet
```

---

# 2. Start PostgreSQL

```bash
docker compose up -d
```

Verify container:

```bash
docker ps
```

---

# 3. Run Spring Boot Application

## Linux / Mac

```bash
./mvnw spring-boot:run
```

## Windows PowerShell

```powershell
.\mvnw spring-boot:run
```

---

# Application URLs

## Backend API

```text
http://localhost:8080
```

## Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

## OpenAPI Docs

```text
http://localhost:8080/v3/api-docs
```

---

# Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/wallet_db
spring.datasource.username=wallet_user
spring.datasource.password=wallet_password
```

---

# Exception Handling

The application uses centralized global exception handling for:

- Validation failures
- Resource not found errors
- Insufficient balance errors
- Invalid transaction errors
- Internal server errors

---

# Validation Rules

## User Validation

- Email must be unique
- Phone number must be unique
- Full name cannot be blank

## Transaction Validation

- Amount must be greater than zero
- Sender and receiver cannot be same
- Sender must have sufficient balance

---

# Design Principles Used

- Layered architecture
- DTO pattern
- Separation of concerns
- Transactional consistency
- Repository pattern
- RESTful API design
- Exception-driven validation
- Production-style entity modeling

---

# Future Improvements

## Security

- JWT Authentication
- OAuth2 Authorization
- Keycloak Integration
- Role-Based Access Control

## Distributed Systems

- Kafka event publishing
- Saga orchestration
- Event sourcing
- Distributed tracing

## Observability

- Prometheus metrics
- Grafana dashboards
- Centralized logging
- Health monitoring

## DevOps

- Jenkins CI/CD pipeline
- Kubernetes deployment
- Helm charts
- Docker image optimization

---

# Author

Siddhant Sharma

- MSc Information Technology
- University of Stuttgart
- Backend Software Engineer with 2+ years of industry experience
