# HR Management System (HRMS)

[![Java](https://img.shields.io/badge/Java-17-orange.svg?style=for-the-badge&logo=openjdk)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen.svg?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg?style=for-the-badge&logo=docker)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square)]()
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green?style=flat-square)]()

---

## 📖 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Configuration](#-configuration)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Testing](#-testing)
- [Monitoring](#-monitoring)
- [Security](#-security)
- [Contributing](#-contributing)
- [License](#-license)

---

## 📋 Overview

**HR Management System (HRMS)** is a enterprise-grade RESTful API for human resources management, built with Spring Boot 3.2.4 and Java 17. The system provides comprehensive employee management, contract handling, and secure authentication with JWT tokens.

### Key Capabilities

- 🔐 **JWT Authentication** - Stateless authentication with access & refresh tokens
- 👥 **Employee Management** - Full CRUD operations for employees
- 📄 **Contract Management** - Handle employment contracts with multiple statuses
- 🏢 **Address Management** - Store and manage employee addresses
- 🔒 **Role-Based Access Control (RBAC)** - ADMIN, MANAGER, and USER roles
- 🗄️ **Database Migrations** - Version-controlled schema with Flyway
- 📊 **Monitoring & Metrics** - Prometheus-ready metrics via Spring Actuator
- 🧪 **Retry Mechanism** - Automatic retry for transient failures with pessimistic locking
- 🛡️ **Soft Delete** - Data preservation with logical deletion
- 📝 **API Documentation** - Interactive Swagger/OpenAPI UI

---

## ✨ Features

### Authentication & Authorization
- ✅ JWT-based stateless authentication
- ✅ Access token (24h) + Refresh token (7 days)
- ✅ BCrypt password hashing (cost factor 12)
- ✅ Role-based authorization (ADMIN, MANAGER, USER)
- ✅ CORS configuration for frontend integration

### Employee Management
- ✅ Create, Read, Update, Delete (CRUD) operations
- ✅ Search and filter capabilities
- ✅ Soft delete implementation
- ✅ Pessimistic locking for concurrent updates
- ✅ Automatic retry on optimistic lock failures

### Contract Management
- ✅ Multiple contract types (FULL_TIME, PART_TIME, CONTRACTOR, INTERN)
- ✅ Contract status tracking (ACTIVE, SUSPENDED, TERMINATED, EXPIRED)
- ✅ Contract terms and conditions storage

### Infrastructure
- ✅ PostgreSQL database with Flyway migrations
- ✅ Docker support for containerized deployment
- ✅ Health checks and metrics endpoints
- ✅ Structured logging with Logback
- ✅ Rate limiting configuration

---

## 🛠️ Tech Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.2.4 |
| **Security** | Spring Security + JWT | 6.x / 0.12.5 |
| **ORM** | Spring Data JPA / Hibernate | 3.x |
| **Database** | PostgreSQL | 15+ |
| **Migrations** | Flyway | 9.x |
| **Validation** | Bean Validation (Jakarta) | 3.x |
| **Documentation** | SpringDoc OpenAPI | 2.4.0 |
| **Metrics** | Micrometer + Prometheus | 1.12.x |
| **Logging** | Logback + Logstash Encoder | 7.4 |
| **Build Tool** | Maven | 3.8+ |
| **Containerization** | Docker | Latest |

---

## 📦 Prerequisites

Before running this project, ensure you have the following installed:

| Tool | Minimum Version | Installation Link |
|------|-----------------|-------------------|
| ☕ JDK | 17 | [Download](https://adoptium.net/) |
| 🐘 PostgreSQL | 15 | [Download](https://www.postgresql.org/download/) |
| 🏗️ Maven | 3.8 | [Download](https://maven.apache.org/) |
| 🐳 Docker (optional) | 20.x | [Download](https://www.docker.com/) |

### Verify Installations

```bash
java -version      # Should show Java 17
mvn -version       # Should show Maven 3.8+
psql --version     # Should show PostgreSQL 15+
docker --version   # Should show Docker 20.x+
```

---

## 🚀 Getting Started

Follow these steps to get HRMS up and running locally.

### Step 1: Clone the Repository

```bash
git clone https://github.com/yannpeclat/HR-Management-System.git
cd HR-Management-System
```

### Step 2: Start PostgreSQL Database

#### Option A: Using Docker (Recommended)

```bash
docker run --name hrms-postgres \
  -e POSTGRES_DB=hrms_dev \
  -e POSTGRES_USER=hrms_dev \
  -e POSTGRES_PASSWORD=hrms_dev_pass \
  -p 5432:5432 \
  -d postgres:15
```

Verify the container is running:
```bash
docker ps | grep hrms-postgres
```

#### Option B: Local PostgreSQL Installation

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create database and user
CREATE DATABASE hrms_dev;
CREATE USER hrms_dev WITH PASSWORD 'hrms_dev_pass';
GRANT ALL PRIVILEGES ON DATABASE hrms_dev TO hrms_dev;
\q
```

### Step 3: Configure Environment Variables (Optional)

You can override default configurations using environment variables:

```bash
# Database configuration
export DATABASE_URL=jdbc:postgresql://localhost:5432/hrms_dev
export DATABASE_USERNAME=hrms_dev
export DATABASE_PASSWORD=hrms_dev_pass

# JWT configuration (REQUIRED for production)
export JWT_SECRET=your-super-secret-key-at-least-32-characters-long

# Server configuration
export SERVER_PORT=8080

# CORS configuration
export CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080

# Rate limiting
export RATE_LIMIT_REQUESTS=100
```

### Step 4: Build the Project

```bash
# Clean and compile
mvn clean compile

# Package (skip tests if needed)
mvn clean package -DskipTests

# Install to local repository
mvn clean install
```

### Step 5: Run the Application

#### Option A: Using Maven

```bash
# Run with default profile (dev)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

#### Option B: Using JAR file

```bash
java -jar target/hr-management-system-1.0.0.jar
```

#### Option C: Using IDE

Open the project in your favorite IDE (IntelliJ IDEA, Eclipse, VS Code) and run `HrmsApplication.java`.

### Step 6: Verify the Application

Once started, verify the application is running:

```bash
# Health check
curl http://localhost:8080/api/actuator/health

# Expected response:
# {"status":"UP","components":{...}}
```

The application will be available at:
- **Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/v3/api-docs`

---

## ⚙️ Configuration

### Active Profiles

The application supports two profiles:

| Profile | Description | Use Case |
|---------|-------------|----------|
| `dev` | Development mode with verbose logging | Local development |
| `prod` | Production mode with optimized settings | Production deployment |

Switch profiles using:
```bash
-Dspring.profiles.active=dev
```

### Configuration Properties

| Property | Description | Default Value | Required |
|----------|-------------|---------------|----------|
| `server.port` | HTTP server port | `8080` | No |
| `spring.datasource.url` | JDBC connection URL | `jdbc:postgresql://localhost:5432/hrms_db` | Yes |
| `spring.datasource.username` | Database username | `hrms_user` | Yes |
| `spring.datasource.password` | Database password | `hrms_pass` | Yes |
| `security.jwt.secret` | JWT signing secret | *(must be set)* | **Yes** |
| `security.jwt.expiration` | Access token TTL (ms) | `86400000` (24h) | No |
| `security.jwt.refresh-expiration` | Refresh token TTL (ms) | `604800000` (7d) | No |
| `cors.allowed-origins` | Allowed CORS origins | `http://localhost:3000,http://localhost:8080` | No |
| `rate-limit.requests-per-minute` | Max requests per minute | `100` | No |

### application.yml Locations

Configuration is loaded from (in order):
1. External `application.yml` (current directory)
2. `src/main/resources/application.yml`
3. Environment variables
4. Command-line arguments

---

## 📡 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Interactive Documentation
Access the Swagger UI at: `http://localhost:8080/api/swagger-ui.html`

---

### 🔓 Public Endpoints

#### Authentication

**POST** `/auth/login` - Authenticate user and obtain JWT tokens

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTYxNjE2MTYxNn0.abc123...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInR5cGUiOiJyZWZyZXNoIiwiZXhwIjoxNjE2MTYxNjE2fQ.xyz789...",
  "username": "admin",
  "role": "ADMIN",
  "expiresIn": 86400000
}
```

**Response Fields:**

|      Field     |  Type  |              Description                       |
|----------------|--------|------------------------------------------------|
|     `token`    | String | Access token (use in Authorization header)     |
| `refreshToken` | String | Refresh token (use to obtain new access token) |
|    `username`  | String | Authenticated username                         |
|      `role`    | String | User role (ADMIN, MANAGER, USER)               |
|    `expiresIn` |  Long  | Token expiration time in milliseconds          |

---

### 🔒 Protected Endpoints

All protected endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Employee Management

**POST** `/employees` - Create a new employee

**Permissions:** `ADMIN`, `MANAGER`

**Request:**
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@company.com",
    "cpf": "123.456.789-00",
    "phone": "+55 11 99999-9999",
    "password": "securePassword123!",
    "address": {
      "street": "123 Main Street",
      "city": "São Paulo",
      "state": "SP",
      "zipCode": "01000-000",
      "country": "Brazil"
    }
  }'
```

**Response (201 Created):**
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "name": "John Doe",
  "email": "john.doe@company.com",
  "cpf": "123.456.789-00",
  "phone": "+55 11 99999-9999",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "address": {
    "id": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22",
    "street": "123 Main Street",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01000-000",
    "country": "Brazil"
  }
}
```

---

**GET** `/employees` - List all employees

**Permissions:** `AUTHENTICATED`

**Request:**
```bash
curl -X GET "http://localhost:8080/api/employees?page=0&size=10&sort=name,asc" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
      "name": "John Doe",
      "email": "john.doe@company.com",
      "status": "ACTIVE",
      "createdAt": "2024-01-15T10:30:00Z"
    },
    {
      "id": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22",
      "name": "Jane Smith",
      "email": "jane.smith@company.com",
      "status": "ACTIVE",
      "createdAt": "2024-01-16T14:20:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 2,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "empty": false
}
```

**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | Integer | 0 | Page number (0-indexed) |
| `size` | Integer | 10 | Items per page |
| `sort` | String | - | Sort field and direction (e.g., `name,asc`) |

---

**GET** `/employees/{id}` - Get employee by ID

**Permissions:** `AUTHENTICATED`

**Request:**
```bash
curl -X GET http://localhost:8080/api/employees/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response (200 OK):**
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "name": "John Doe",
  "email": "john.doe@company.com",
  "cpf": "123.456.789-00",
  "phone": "+55 11 99999-9999",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z",
  "address": {
    "id": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22",
    "street": "123 Main Street",
    "city": "São Paulo",
    "state": "SP",
    "zipCode": "01000-000",
    "country": "Brazil"
  },
  "contracts": [
    {
      "id": "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33",
      "type": "FULL_TIME",
      "status": "ACTIVE",
      "startDate": "2024-01-15",
      "salary": 5000.00
    }
  ]
}
```

---

**PUT** `/employees/{id}` - Update an employee

**Permissions:** `ADMIN`, `MANAGER`

**Request:**
```bash
curl -X PUT http://localhost:8080/api/employees/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "John Doe Updated",
    "phone": "+55 11 88888-8888",
    "address": {
      "street": "456 New Avenue",
      "city": "Rio de Janeiro",
      "state": "RJ",
      "zipCode": "20000-000",
      "country": "Brazil"
    }
  }'
```

**Response (200 OK):**
```json
{
  "id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "name": "John Doe Updated",
  "email": "john.doe@company.com",
  "cpf": "123.456.789-00",
  "phone": "+55 11 88888-8888",
  "status": "ACTIVE",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T15:45:00Z",
  "address": {
    "id": "b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22",
    "street": "456 New Avenue",
    "city": "Rio de Janeiro",
    "state": "RJ",
    "zipCode": "20000-000",
    "country": "Brazil"
  }
}
```

---

**DELETE** `/employees/{id}` - Soft delete an employee

**Permissions:** `ADMIN`

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/employees/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Response (204 No Content):**
```
(Empty body)
```

---

### HTTP Status Codes

| Code | Description | When Returned |
|------|-------------|---------------|
| `200` | OK | Successful GET, PUT, PATCH |
| `201` | Created | Successful resource creation (POST) |
| `204` | No Content | Successful deletion |
| `400` | Bad Request | Invalid input or validation error |
| `401` | Unauthorized | Missing or invalid JWT token |
| `403` | Forbidden | Insufficient permissions |
| `404` | Not Found | Resource does not exist |
| `409` | Conflict | Duplicate resource or version conflict |
| `500` | Internal Server Error | Unexpected server error |

---

### Error Response Format

```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11",
  "path": "/api/employees/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
}
```

---

## 🗂️ Project Structure

```
HR-Management-System/
├── .gitignore                    # Git ignore rules
├── pom.xml                       # Maven build configuration
├── docker-compose.yml            # Docker Compose configuration
├── Dockerfile                    # Docker image definition
├── README.md                     # This file
├── LICENSE                       # MIT License
│
├── src/
│   ├── main/
│   │   ├── java/com/hrms/
│   │   │   ├── HrmsApplication.java          # Main application entry point
│   │   │   │
│   │   │   ├── config/                       # Application configuration
│   │   │   │   ├── SecurityConfig.java       # Spring Security configuration
│   │   │   │   └── RetryConfig.java          # Retry mechanism configuration
│   │   │   │
│   │   │   ├── controller/                   # REST controllers
│   │   │   │   ├── AuthenticationController.java  # Login/logout endpoints
│   │   │   │   └── EmployeeController.java   # Employee CRUD endpoints
│   │   │   │
│   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java         # Login request DTO
│   │   │   │   ├── LoginResponse.java        # Login response DTO
│   │   │   │   ├── EmployeeRequest.java      # Employee create/update DTO
│   │   │   │   └── EmployeeResponse.java     # Employee response DTO
│   │   │   │
│   │   │   ├── entity/                       # JPA entities
│   │   │   │   ├── User.java                 # User entity
│   │   │   │   ├── Employee.java             # Employee entity
│   │   │   │   ├── Contract.java             # Contract entity
│   │   │   │   ├── ContractTerms.java        # Contract terms entity
│   │   │   │   └── Address.java              # Address entity
│   │   │   │
│   │   │   ├── enums/                        # Enumerations
│   │   │   │   ├── UserRole.java             # USER, MANAGER, ADMIN
│   │   │   │   ├── EmployeeStatus.java       # ACTIVE, INACTIVE, TERMINATED
│   │   │   │   ├── ContractType.java         # FULL_TIME, PART_TIME, etc.
│   │   │   │   └── ContractStatus.java       # ACTIVE, SUSPENDED, etc.
│   │   │   │
│   │   │   ├── exception/                    # Exception handling
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │
│   │   │   ├── repository/                   # JPA repositories
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── EmployeeRepository.java
│   │   │   │
│   │   │   ├── security/                     # JWT security components
│   │   │   │   ├── JwtTokenProvider.java     # JWT token generation/validation
│   │   │   │   ├── JwtAuthenticationFilter.java  # JWT filter
│   │   │   │   └── CorrelationIdFilter.java  # Request correlation
│   │   │   │
│   │   │   └── service/                      # Business logic
│   │   │       ├── EmployeeService.java      # Employee business logic
│   │   │       └── UserDetailsServiceImpl.java  # Spring Security user details
│   │   │
│   │   └── resources/
│   │       ├── application.yml               # Application configuration
│   │       ├── logback-spring.xml            # Logging configuration
│   │       └── db/
│   │           └── migration/                # Flyway migration scripts
│   │               └── V1__initial_schema.sql
│   │
│   └── test/
│       └── java/com/hrms/
│           ├── controller/                   # Controller tests
│           └── service/                      # Service tests
│
└── target/                                   # Build output (generated)
```

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Tests with Coverage Report

```bash
# Add JaCoCo plugin to pom.xml if not present, then run:
mvn clean test jacoco:report
```

View coverage report at: `target/site/jacoco/index.html`

### Run Specific Test Class

```bash
mvn test -Dtest=EmployeeControllerTest
```

### Run Tests with Profile

```bash
mvn test -Dspring.profiles.active=test
```

### Test Configuration

Tests use H2 in-memory database for fast execution. Configuration is in:
- `src/test/resources/application-test.yml`

---

## 📊 Monitoring

Spring Actuator exposes operational endpoints for monitoring and management.

### Available Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/actuator/health` | GET | Application health status |
| `/actuator/info` | GET | Application information |
| `/actuator/metrics` | GET | All application metrics |
| `/actuator/prometheus` | GET | Metrics in Prometheus format |
| `/actuator/env` | GET | Environment properties |

### Example Health Check

```bash
curl http://localhost:8080/api/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "vendor": "PostgreSQL"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Prometheus Integration

Add to your `prometheus.yml`:

```yaml
scrape_configs:
  - job_name: 'hrms'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/api/actuator/prometheus'
```

---

## 🔒 Security

### Default Credentials

| Username | Password   | Role |
|----------|------------|------|
| `admin` | `admin123` | ADMIN |

> ⚠️ **WARNING**: Change default credentials immediately in production!

### Security Best Practices

1. **JWT Secret**: Use a strong, randomly generated secret (minimum 32 characters)
   ```bash
   export JWT_SECRET=$(openssl rand -base64 32)
   ```

2. **HTTPS**: Always use HTTPS in production environments

3. **CORS**: Restrict allowed origins to your frontend domains only

4. **Rate Limiting**: Adjust based on your expected traffic:
   ```bash
   export RATE_LIMIT_REQUESTS=100
   ```

5. **Database**: Use strong passwords and restrict database access

6. **Passwords**: All passwords are hashed with BCrypt (cost factor 12)

### JWT Token Flow

```
┌─────────────┐      POST /auth/login       ┌─────────────┐
│   Client    │ ──────────────────────────▶ │   Server    │
│             │                             │             │
│             │ ◀───── Access + Refresh ─── │             │
│             │         Tokens              │             │
└─────────────┘                             └─────────────┘
       │                                           │
       │  GET /employees                           │
       │  Authorization: Bearer <token>            │
       ├──────────────────────────────────────────▶│
       │                                           │
       │  200 OK + Employee Data                   │
       │◀──────────────────────────────────────────┤
       │                                           │
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

### How to Contribute

1. **Fork** the repository
   ```bash
   git fork https://github.com/yannpeclat/HR-Management-System.git
   ```

2. **Clone** your fork
   ```bash
   git clone https://github.com/YOUR_USERNAME/HR-Management-System.git
   cd HR-Management-System
   ```

3. **Create** a feature branch
   ```bash
   git checkout -b feature/amazing-feature
   ```

4. **Make** your changes
    - Write clean, readable code
    - Add tests for new functionality
    - Update documentation as needed

5. **Commit** your changes
   ```bash
   git commit -m "feat: add amazing feature"
   ```

   Follow [Conventional Commits](https://www.conventionalcommits.org/) format:
    - `feat:` New feature
    - `fix:` Bug fix
    - `docs:` Documentation update
    - `refactor:` Code refactoring
    - `test:` Adding tests
    - `chore:` Maintenance tasks

6. **Push** to your branch
   ```bash
   git push origin feature/amazing-feature
   ```

7. **Open** a Pull Request
    - Describe your changes
    - Reference any related issues
    - Ensure all tests pass

### Code Style

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Keep methods small and focused
- Add JavaDoc for public APIs

### Reporting Issues

Found a bug? Have a feature request? Please open an issue with:
- Clear description
- Steps to reproduce (for bugs)
- Expected vs actual behavior
- Screenshots if applicable

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 HR Management System

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 📞 Support

- 📧 Email: [yannpeclat@gmail.com](mailto:yannpeclat@gmail.com)
- 💬 Discord: peclatt#3450
- 💬 Issues: [GitHub Issues](https://github.com/yannpeclat/HR-Management-System/issues)
- 📖 Wiki: [Project Wiki](https://github.com/yannpeclat/HR-Management-System/wiki)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- JWT community for the jjwt library
- PostgreSQL team for the robust database
- All contributors to this project

---

<div align="center">

**Made with ❤️ for me *Yann Peclat* using Java & Spring Boot**

[⬆️ Back to Top](#-table-of-contents)

</div>