# HR Management System
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow)](https://opensource.org/licenses/MIT)
## 📖 Table of Contents
- [📋 Overview](#-overview)
- [✨ Features](#-features)
- [🛠️ Technologies](#️-technologies)
- [⚡ Prerequisites](#-prerequisites)
- [🚀 Getting Started](#-getting-started)
- [🔧 Configuration](#-configuration)
- [🌐 API Documentation](#-api-documentation)
- [📁 Project Structure](#-project-structure)
- [🧪 Testing](#-testing)
- [📊 Monitoring](#-monitoring)
- [🔐 Security](#-security)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [👥 Authors](#-authors)
- [🙏 Acknowledgments](#-acknowledgments)
---
## 📋 Overview
The **HR Management System (HRMS)** is a comprehensive human resources management platform built with modern enterprise technologies. It provides secure employee management, contract tracking, and role-based access control with JWT authentication.
### What It Does
- 👥 **Employee Management** - Complete CRUD operations for employee records
- 🏢 **Address Management** - Manage employee addresses and locations
- 📄 **Contract Management** - Track employment contracts and terms
- 🔐 **Secure Authentication** - JWT-based stateless authentication
- 👑 **Role-Based Access** - ADMIN and USER roles with different permissions
- 🗄️ **Database Migrations** - Automated schema management with Flyway
- 📚 **API Documentation** - Interactive Swagger/OpenAPI documentation
- 🐳 **Docker Ready** - Easy deployment with Docker Compose
---
## ✨ Features
### Core Functionality
- **Employee CRUD Operations**
    - Create, read, update, and delete employee records
    - Pagination and sorting support
    - Validation and error handling
- **Authentication & Authorization**
    - JWT token-based authentication
    - Access and refresh token mechanism
    - Role-based access control (ADMIN/USER)
    - Secure password encoding
- **Data Management**
    - Employee personal information
    - Address management
    - Contract tracking
    - Department and position management
- **Infrastructure**
    - RESTful API design
    - Database migration with Flyway
    - Docker containerization
    - Health checks and monitoring
---
## 🛠️ Technologies
| Technology | Version | Description |
|------------|---------|-------------|
| **Java** | 17+ | Core programming language |
| **Spring Boot** | 3.2.4 | Application framework |
| **Spring Security** | 6.x | Security framework |
| **Spring Data JPA** | 3.x | Data persistence |
| **PostgreSQL** | 15 | Primary database |
| **Flyway** | Latest | Database migrations |
| **JWT** | Latest | Token authentication |
| **Lombok** | Latest | Code generation |
| **Swagger/OpenAPI** | Latest | API documentation |
| **Docker** | Latest | Containerization |
| **Maven** | 3.9.x | Build automation |
---
## ⚡ Prerequisites
Before you begin, ensure you have the following installed:
### Required Software
- **[Java JDK 17](https://adoptium.net/)** or higher
- **[Apache Maven 3.9](https://maven.apache.org/)** or higher
- **[Docker](https://www.docker.com/)** and Docker Compose
- **[Git](https://git-scm.com/)** for version control
### Optional Tools
- **[PostgreSQL](https://www.postgresql.org/)** (if running without Docker)
- **[Postman](https://www.postman.com/)** or similar API testing tool
- IDE: IntelliJ IDEA, Eclipse, or VS Code
### Verify Installations
```bash
# Check Java version
java -version
# Check Maven version
mvn -version
# Check Docker version
docker --version
docker-compose --version
# Check Git version
git --version
```
---
## 🚀 Getting Started
Follow these steps to get the project running locally:
### 1️⃣ Clone the Repository
```bash
git clone https://github.com/yannpeclat/HR-Management-System.git
cd HR-Management-System
```
### 2️⃣ Database Setup
#### Option A: Using Docker Compose (Recommended)
```bash
# Start PostgreSQL and the application
docker-compose up -d
# View logs
docker-compose logs -f
# Stop all services
docker-compose down
```
#### Option B: Manual PostgreSQL Setup
1. Install PostgreSQL 15
2. Create database and user:
```sql
CREATE DATABASE hrms_db;
CREATE USER hrms_user WITH PASSWORD 'hrms_password';
GRANT ALL PRIVILEGES ON DATABASE hrms_db TO hrms_user;
```
3. Update `src/main/resources/application.yml` with your credentials
### 3️⃣ Build the Application
```bash
# Clean and install dependencies
mvn clean install
# Skip tests (optional)
mvn clean install -DskipTests
```
### 4️⃣ Run the Application
```bash
# Using Maven
mvn spring-boot:run
# Or run the compiled JAR
java -jar target/hr-management-system-0.0.1-SNAPSHOT.jar
```
### 5️⃣ Verify the Application
The application will start on `http://localhost:8080`
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
### 6️⃣ Default Login Credentials
| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin123` | ADMIN |
> ⚠️ **Important**: Change the default password in production!
---
## 🔧 Configuration
### Environment Variables
You can configure the application using environment variables:
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hrms_db
SPRING_DATASOURCE_USERNAME=hrms_user
SPRING_DATASOURCE_PASSWORD=hrms_password
# JWT Configuration
JWT_SECRET_KEY=your-super-secret-jwt-key-minimum-256-bits-long
JWT_ACCESS_TOKEN_VALIDITY=3600000
JWT_REFRESH_TOKEN_VALIDITY=86400000
# Server Configuration
SERVER_PORT=8080
```
### Application Profiles
The application supports multiple profiles:
- **`dev`** - Development environment with detailed logging
- **`prod`** - Production environment with optimized settings
  Activate a profile:
```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# Using Java
java -jar -Dspring.profiles.active=prod target/hr-management-system-0.0.1-SNAPSHOT.jar
```
### Configuration Files
- `application.yml` - Base configuration
- `application-dev.yml` - Development-specific settings
- `application-prod.yml` - Production-specific settings
---
## 🌐 API Documentation
### Base URL
```
http://localhost:8080/api
```
### Interactive Documentation
Access the Swagger UI at: **http://localhost:8080/swagger-ui.html**
### Authentication Endpoints
#### 🔐 Login
**Endpoint:** `POST /api/auth/login`
**Request:**
```json
{
  "username": "admin",
  "password": "password"
}
```
**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```
#### 🔄 Refresh Token
**Endpoint:** `POST /api/auth/refresh`
**Headers:**
```
Authorization: Bearer {refreshToken}
```
### Employee Endpoints
All employee endpoints require authentication with a valid JWT token.
#### 📋 Get All Employees
**Endpoint:** `GET /api/employees`
**Query Parameters:**
- `page` (default: 0) - Page number
- `size` (default: 10) - Items per page
- `sort` (optional) - Sort field and direction
  **Headers:**
```
Authorization: Bearer {accessToken}
```
**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@company.com",
      "position": "Software Engineer",
      "department": "Engineering",
      "hireDate": "2024-01-15",
      "salary": 75000.00,
      "address": {
        "street": "123 Main St",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA"
      }
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
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
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
#### 🔍 Get Employee by ID
**Endpoint:** `GET /api/employees/{id}`
**Headers:**
```
Authorization: Bearer {accessToken}
```
#### ➕ Create Employee
**Endpoint:** `POST /api/employees`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer {accessToken}
```
**Request:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@company.com",
  "position": "Product Manager",
  "department": "Product",
  "hireDate": "2024-03-01",
  "salary": 85000.00,
  "address": {
    "street": "456 Oak Ave",
    "city": "San Francisco",
    "state": "CA",
    "zipCode": "94102",
    "country": "USA"
  }
}
```
#### ✏️ Update Employee
**Endpoint:** `PUT /api/employees/{id}`
**Headers:**
```
Content-Type: application/json
Authorization: Bearer {accessToken}
```
**Request:** (same as Create)
#### 🗑️ Delete Employee
**Endpoint:** `DELETE /api/employees/{id}`
**Headers:**
```
Authorization: Bearer {accessToken}
```
### HTTP Status Codes
| Code | Description |
|------|-------------|
| `200 OK` | Request successful |
| `201 Created` | Resource created successfully |
| `400 Bad Request` | Invalid request data |
| `401 Unauthorized` | Missing or invalid token |
| `403 Forbidden` | Insufficient permissions |
| `404 Not Found` | Resource not found |
| `500 Internal Server Error` | Server error |
### Error Response Format
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/employees",
  "details": [
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "must be a well-formed email address"
    }
  ]
}
```
---
## 📁 Project Structure
```
HR-Management-System/
├── .mvn/                          # Maven wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/com/hrms/
│   │   │   ├── HrmsApplication.java          # Main application entry point
│   │   │   ├── config/                       # Configuration classes
│   │   │   │   ├── SecurityConfig.java       # Spring Security configuration
│   │   │   │   ├── JwtConfig.java            # JWT configuration
│   │   │   │   └── SwaggerConfig.java        # Swagger/OpenAPI configuration
│   │   │   ├── controller/                   # REST controllers
│   │   │   │   ├── AuthController.java       # Authentication endpoints
│   │   │   │   └── EmployeeController.java   # Employee CRUD endpoints
│   │   │   ├── service/                      # Business logic layer
│   │   │   │   ├── AuthService.java          # Authentication service
│   │   │   │   ├── EmployeeService.java      # Employee business logic
│   │   │   │   └── JwtService.java           # JWT token service
│   │   │   ├── model/                        # Data models
│   │   │   │   ├── entity/                   # JPA entities
│   │   │   │   │   ├── Employee.java         # Employee entity
│   │   │   │   │   ├── Address.java          # Address entity
│   │   │   │   │   └── Contract.java         # Contract entity
│   │   │   │   └── dto/                      # Data Transfer Objects
│   │   │   │       ├── AuthRequest.java      # Login request DTO
│   │   │   │       ├── AuthResponse.java     # Login response DTO
│   │   │   │       └── EmployeeDTO.java      # Employee DTO
│   │   │   ├── repository/                   # Data access layer
│   │   │   │   ├── EmployeeRepository.java   # Employee JPA repository
│   │   │   │   └── UserRepository.java       # User JPA repository
│   │   │   └── security/                     # Security components
│   │   │       ├── JwtFilter.java            # JWT request filter
│   │   │       ├── JwtUtil.java              # JWT utility methods
│   │   │       └── CustomUserDetailsService.java  # User details service
│   │   └── resources/
│   │       ├── application.yml               # Main configuration
│   │       ├── application-dev.yml           # Development configuration
│   │       ├── application-prod.yml          # Production configuration
│   │       └── db/
│   │           └── migration/                # Flyway migration scripts
│   │               └── V1__initial_schema.sql
│   └── test/
│       └── java/com/hrms/
│           ├── controller/                   # Controller tests
│           ├── service/                      # Service tests
│           └── integration/                  # Integration tests
├── docker-compose.yml                        # Docker Compose configuration
├── Dockerfile                                # Docker image configuration
├── pom.xml                                   # Maven project configuration
├── README.md                                 # This file
└── .gitignore                               # Git ignore rules
```
---
## 🧪 Testing
### Run All Tests
```bash
# Run all tests
mvn test
# Run with coverage report
mvn clean verify
```
### Run Specific Test Classes
```bash
# Run specific test class
mvn test -Dtest=EmployeeServiceTest
# Run all controller tests
mvn test -Dtest="*ControllerTest"
```
### Integration Tests
```bash
# Run integration tests
mvn failsafe:integration-test
# Run with profile
mvn verify -Dspring.profiles.active=test
```
### Test Coverage
View coverage reports in:
- `target/site/jacoco/index.html` (HTML report)
- `target/surefire-reports/` (XML reports)
### Running Tests with Docker
```bash
# Start test database
docker-compose -f docker-compose.test.yml up -d
# Run tests
mvn clean verify
# Stop test database
docker-compose -f docker-compose.test.yml down
```
---
## 📊 Monitoring
Spring Boot Actuator provides production-ready monitoring features.
### Available Endpoints
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/actuator/health` | GET | Application health status |
| `/actuator/info` | GET | Application information |
| `/actuator/metrics` | GET | Application metrics |
| `/actuator/env` | GET | Environment properties |
| `/actuator/loggers` | GET/POST | Logger configuration |
| `/actuator/threaddump` | GET | Thread dump |
### Health Check Example
```bash
curl http://localhost:8080/actuator/health
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
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 200000000000,
        "threshold": 10485760
      }
    }
  }
}
```
### Metrics Example
```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```
---
## 🔐 Security
### Authentication Flow
1. User sends login credentials to `/api/auth/login`
2. Server validates credentials against database
3. Server generates JWT access token and refresh token
4. Tokens are returned to client
5. Client includes access token in `Authorization` header for subsequent requests
6. When access token expires, use refresh token to get new access token
### JWT Token Structure
- **Access Token**: Short-lived (default: 1 hour)
- **Refresh Token**: Long-lived (default: 24 hours)
### Security Headers
Always include in authenticated requests:
```
Authorization: Bearer {your-access-token}
Content-Type: application/json
```
### Security Best Practices
✅ **Do:**
- Use HTTPS in production
- Rotate JWT secret keys regularly
- Implement rate limiting
- Validate all inputs
- Use strong passwords
- Keep dependencies updated
  ❌ **Don't:**
- Store tokens in localStorage (use httpOnly cookies)
- Commit sensitive data to Git
- Use default passwords in production
- Disable security features
- Ignore security warnings
### Role-Based Access Control
| Role | Permissions |
|------|-------------|
| `ADMIN` | Full access to all endpoints |
| `USER` | Read-only access to employee data |
---
## 🤝 Contributing
We welcome contributions! Here's how you can help:
### Getting Started
1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes**
4. **Add tests** for new functionality
5. **Ensure all tests pass**
   ```bash
   mvn clean verify
   ```
6. **Commit your changes** using conventional commits
   ```bash
   git commit -m "feat: add amazing feature"
   ```
7. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
8. **Open a Pull Request**
### Conventional Commits
We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `style:` - Code style changes (formatting)
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks
### Code Style Guidelines
- Follow [Oracle Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-introduction.html)
- Use meaningful variable and method names
- Write clear, concise comments
- Keep methods small and focused
- Maintain high test coverage
### Reporting Issues
When reporting issues, please include:
- Clear title and description
- Steps to reproduce
- Expected vs actual behavior
- Environment details (OS, Java version, etc.)
- Screenshots or logs if applicable
### Pull Request Guidelines
- Keep PRs focused and small
- Include tests for new features
- Update documentation as needed
- Ensure CI/CD pipeline passes
- Request review from maintainers
---
## 📄 License
This project is licensed under the [MIT License](LICENSE).
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
## 👥 Authors
- **Yann Peclat** - [@yannpeclat](https://github.com/yannpeclat)
  See also the list of [contributors](https://github.com/yannpeclat/HR-Management-System/contributors) who participated in this project.
---
## 🙏 Acknowledgments
- [Spring Boot Team](https://spring.io/projects/spring-boot) for the excellent framework
- [PostgreSQL Community](https://www.postgresql.org/community/) for the reliable database
- [Swagger Team](https://swagger.io/) for API documentation tools
- [Docker Team](https://www.docker.com/) for containerization technology
- All contributors and supporters of this project
---
## 📞 Support
Need help? Here's how to reach us:
- 📧 **Email**: [yannpeclat@gmail.com](mailto:yannpeclat@gmail.com)
- 💬 **Discord**: peclatt#3450
- 🐛 **Issues**: [GitHub Issues](https://github.com/yannpeclat/HR-Management-System/issues)
### FAQ
**Q: How do I reset the admin password?**  
A: You can update it directly in the database or use the password reset endpoint if implemented.
**Q: Can I use MySQL instead of PostgreSQL?**  
A: Yes, update the dependency in `pom.xml` and adjust the `application.yml` configuration.
**Q: How do I deploy to production?**  
A: Use the `prod` profile and deploy using Docker or your preferred cloud platform.
**Q: Is there a frontend application?**  
A: Currently, this is a backend-only project. Frontend development is planned for future releases.
---
<div align="center">
**Made with ❤️ for better HR Management**
If you like this project, please ⭐ star this repository!
</div>