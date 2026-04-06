# 🚀 HR Management System - Enterprise Contract & Employee Manager

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-blue?style=for-the-badge&logo=java)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Production%20Ready-brightgreen?style=for-the-badge)

**A robust, scalable, and enterprise-grade Human Resources Management System built with Clean Architecture principles.**

[Features](#-features) • [Architecture](#-architecture) • [Getting Started](#-getting-started) • [Usage](#-usage) • [Business Rules](#-business-rules)

</div>

---

## 🎯 Overview

This **HR Management System** is a comprehensive solution designed to manage employees, contracts, and organizational workflows with strict adherence to business rules and data integrity. Built using **Clean Architecture** principles, it ensures separation of concerns, testability, and maintainability.

The system handles the complete lifecycle of employee management—from onboarding and contract creation to termination and historical reporting—while enforcing critical validations such as CPF uniqueness, email format, age restrictions, and single active contract per employee.

---

## ✨ Features

### 👥 Employee Management Module
- **Full CRUD Operations**: Create, Read, Update, and Soft-Delete employees
- **Advanced Validation**: CPF validation (format + uniqueness), Email format, Minimum age (≥ 16 years)
- **Smart Search**: Find employees by ID or CPF
- **Status Management**: Active/Inactive tracking with soft delete pattern
- **Address Management**: Complete address structure (Street, City, State, ZIP, Country)

### 📄 Contract Management Module
- **Multiple Contract Types**: CLT, PJ (Contractor), Temporary, Internship
- **Contract Lifecycle**: Creation, modification, and termination workflows
- **Business Rule Enforcement**: Only 1 active contract per employee, Cannot create contracts for inactive employees
- **Rich Contract Terms**: Benefits, bonus policies, vacation days, termination clauses
- **Financial Tracking**: Salary, currency, weekly workload management

### 📊 Dashboard & Analytics
- **Real-time Metrics**: Total employees (Active vs Inactive), Contract statistics (Active, Expired, Expiring Soon)
- **Quick Insights**: At-a-glance organizational health indicators

### 📑 Reporting System
- **Pre-built Reports**: Active employees list, Active contracts overview, Employee contract history, Payroll summary
- **Filter Capabilities**: By status, type, date ranges

### 🔒 Data Integrity & Security
- **UUID-based ID Generation**: Secure, unique identifiers
- **Soft Delete Pattern**: Data retention for audit trails
- **Immutable Historical Records**: Prevents data tampering

---

## 🏗️ Architecture

This project follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────────────────────────────────────┐
│              APPLICATION LAYER                  │
│  (Main Menu, User Input/Output Handling)        │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│              CONTROLLER LAYER                   │
│  (Request Validation, Response Formatting)      │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│               SERVICE LAYER                     │
│  (Business Logic, Rules Enforcement)            │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│             REPOSITORY LAYER                    │
│  (Data Access, In-Memory Storage)               │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│               DOMAIN LAYER                      │
│  (Entities, Enums, Validators, Core Models)     │
└─────────────────────────────────────────────────┘
```

| Layer | Responsibility |
|-------|---------------|
| **Application** | Main entry point, menu navigation, user interaction |
| **Controller** | Input validation, orchestrates service calls, formats output |
| **Service** | Implements business rules, validates operations, manages workflows |
| **Repository** | Data persistence (in-memory), CRUD operations |
| **Domain** | Core entities, enums, validators, business objects |

---

## 🛠️ Tech Stack

- **Language**: Java 17+
- **Architecture**: Clean Architecture / Hexagonal Architecture
- **Design Patterns**: Repository Pattern, Service Layer, Factory Pattern (UUID)
- **Validation**: Custom validators for CPF, Email, Dates, Business Rules
- **Build Tool**: Manual compilation (javac) or Maven/Gradle ready

---

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Git

### Installation

```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/hr-management-system.git
cd hr-management-system

# Compile all source files
mkdir -p bin
javac -d bin $(find src -name "*.java")

# Run the application
java -cp bin application.Main
```

---

## 💻 Usage

Upon running, you'll see the **Main Menu**:

```
========================================
    HR MANAGEMENT SYSTEM - MAIN MENU
========================================
1. Manage Employees
2. Manage Contracts
3. Dashboard (Overview)
4. Reports
5. Exit
========================================
```

### Employee Management
- Register, List, Search, Update, Deactivate employees
- Validates CPF uniqueness, email format, minimum age (≥16)
- Auto-generates UUID, manages ACTIVE/INACTIVE status

### Contract Management
- Create, List, Search, Update, Terminate contracts
- Types: CLT, PJ, TEMPORARY, INTERNSHIP
- Ensures only 1 active contract per employee
- Blocks contracts for inactive employees

### Dashboard
Real-time metrics: Total employees, Active/Inactive counts, Contract statistics

### Reports
Generate filtered lists: Active employees, Active contracts, Employee history, Payroll summary

---

## 📜 Business Rules

### Employee Rules
| Rule | Description |
|------|-------------|
| **CPF Uniqueness** | CPF must be unique across all employees |
| **Email Format** | Must contain "@" symbol |
| **Minimum Age** | Employee must be ≥ 16 years old |
| **Soft Delete** | Employees are never deleted, only deactivated |

### Contract Rules
| Rule | Description |
|------|-------------|
| **Single Active Contract** | An employee can have only ONE active contract at a time |
| **Employee Status Check** | Cannot create contracts for INACTIVE employees |
| **No Hard Deletes** | Contracts are never removed from the system |
| **Irreversible Termination** | Once terminated, a contract cannot be reactivated |
| **Salary Validation** | Salary must be > 0 |

---

## 📁 Project Structure

```
hr-management-system/
├── src/
│   ├── application/          # Main.java (entry point)
│   ├── controller/           # EmployeeController, ContractController
│   ├── service/              # EmployeeService, ContractService
│   ├── repository/           # EmployeeRepository, ContractRepository
│   └── domain/
│       ├── entities/         # Employee, Contract, Address, ContractTerms
│       ├── enums/            # EmployeeStatus, ContractStatus, ContractType
│       └── validators/       # CPFValidator, EmailValidator, DateValidator
├── bin/                      # Compiled classes
└── README.md
```

---

## 🔮 Future Enhancements

- [ ] Database Integration (PostgreSQL/MySQL)
- [ ] REST API with Spring Boot
- [ ] Authentication & Authorization
- [ ] PDF/CSV Export
- [ ] Unit Tests (JUnit 5)
- [ ] Docker Support
- [ ] Frontend UI (React/Angular)

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

MIT License - feel free to use this project for learning or production.

---

## 👨‍💻 Author

**Your Name**  
*Software Engineer | Java Specialist | Clean Architecture Enthusiast*

- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Profile](https://linkedin.com/in/yourprofile)

---

<div align="center">

**⭐ If you found this project helpful, please give it a star!**

*Built with ❤️ using Java and Clean Architecture principles*

</div>
