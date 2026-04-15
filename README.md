# 🚀 HR Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-blue?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring-boot)
![Architecture](https://img.shields.io/badge/Architecture-Layered-green?style=for-the-badge)
![Database](https://img.shields.io/badge/Database-PostgreSQL%2FH2-blue?style=for-the-badge&logo=postgresql)

**Sistema de Gestão de Recursos Humanos com Spring Boot 3 e API REST**

[Visão Geral](#-visão-geral) • [Tecnologias](#-tecnologias) • [Como Rodar](#-como-rodar) • [API Endpoints](#-api-endpoints) • [Configuração](#-configuração)

</div>

---

## 🎯 Visão Geral

O **HR Management System** é uma API REST completa para gestão de funcionários e contratos de trabalho. Desenvolvido com Spring Boot 3, o sistema oferece endpoints seguros com autenticação JWT, validações de negócio e migrações de banco de dados versionadas com Flyway.

### Funcionalidades Principais

- 👥 **Gestão de Funcionários**: CRUD completo com validações (CPF, email, idade mínima)
- 📄 **Gestão de Contratos**: Múltiplos tipos (CLT, PJ, Temporário, Estágio)
- 🔐 **Autenticação JWT**: Login seguro com refresh token
- 📊 **Dashboard**: Métricas em tempo real
- 🗄️ **Migrações**: Scripts Flyway versionados

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| Java | 17+ | Linguagem principal |
| Spring Boot | 3.2.0 | Framework principal |
| Spring Security | 6.x | Autenticação e autorização |
| Spring Data JPA | 3.x | Persistência de dados |
| Flyway | 9.x | Migrações de banco de dados |
| JWT | 0.11.5 | Tokens de autenticação |
| H2 Database | - | Banco em memória (dev) |
| PostgreSQL | - | Banco de dados (prod) |
| Maven | 3.x | Gerenciador de dependências |

---

## 🚀 Como Rodar

### Pré-requisitos

- JDK 17 ou superior
- Maven 3.6+
- PostgreSQL (para produção) ou H2 (desenvolvimento)

### Opção 1: Rodar com Maven (Recomendado para desenvolvimento)

```bash
# Clone o repositório
git clone <URL_DO_REPOSITORIO>
cd hr-management-system

# Compile o projeto
mvn clean install

# Rode a aplicação (usando H2 em memória)
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

### Opção 2: Rodar via Docker (Se disponível)

```bash
# Build da imagem Docker
docker build -t hrms .

# Rodar o container
docker run -p 8080:8080 hrms
```

### Opção 3: Executar JAR compilado

```bash
# Compile o projeto
mvn clean package

# Execute o JAR
java -jar target/hr-management-system-1.0.0.jar
```

---

## ⚙️ Configuração

### Perfis Disponíveis

O projeto possui dois perfis de configuração:

| Profile | Banco de Dados | Uso |
|---------|----------------|-----|
| `dev` | H2 (em memória) | Desenvolvimento local |
| `prod` | PostgreSQL | Produção |

### Variáveis de Ambiente (Produção)

Para rodar em produção, configure as seguintes variáveis de ambiente:

```bash
# Perfil
export SPRING_PROFILES_ACTIVE=prod

# Database
export DATABASE_URL=jdbc:postgresql://localhost:5432/hrdb
export DATABASE_USERNAME=seu_usuario
export DATABASE_PASSWORD=sua_senha

# JWT (mínimo 32 caracteres)
export JWT_SECRET=$(openssl rand -base64 32)

# CORS
export CORS_ALLOWED_ORIGINS=https://app.seudominio.com

# Senhas dos usuários
export ADMIN_DEFAULT_PASSWORD=sua_senha_admin
```

### Rodando com profile dev (padrão)

```bash
# Sem necessidade de configuração adicional
mvn spring-boot:run
```

---

## 📡 API Endpoints

### Autenticação

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Realizar login |
| POST | `/api/auth/refresh` | Refresh token |

### Funcionários

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/employees` | Listar funcionários |
| GET | `/api/employees/{id}` | Buscar funcionário por ID |
| POST | `/api/employees` | Criar funcionário |
| PUT | `/api/employees/{id}` | Atualizar funcionário |
| DELETE | `/api/employees/{id}` | Inativar funcionário |

### Contratos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/contracts` | Listar contratos |
| GET | `/api/contracts/{id}` | Buscar contrato por ID |
| POST | `/api/contracts` | Criar contrato |
| PUT | `/api/contracts/{id}` | Atualizar contrato |
| DELETE | `/api/contracts/{id}` | Terminar contrato |

---

## 📁 Estrutura do Projeto

```
hr-management-system/
├── src/main/java/com/hrms/
│   ├── config/           # Configurações (Security, JWT, CORS)
│   ├── controller/       # Controllers REST
│   ├── domain/
│   │   ├── entity/       # Entidades JPA
│   │   └── enums/        # Enumerações
│   ├── dto/              # Data Transfer Objects
│   ├── exception/        # Exception handlers
│   ├── repository/       # Repositórios JPA
│   └── service/          # Services com regras de negócio
├── src/main/resources/
│   ├── db/migration/     # Scripts Flyway
│   ├── application.yml   # Configuração dev
│   └── application-prod.yml # Configuração prod
├── pom.xml
└── README.md
```

---

## 🗄️ Banco de Dados

O projeto utiliza **Flyway** para versionamento do schema do banco de dados. Os scripts estão em `src/main/resources/db/migration/`:

- `V1__initial_schema.sql` - Criação das tabelas
- `V2__seed_initial_data.sql` - Dados iniciais

---

## 🔒 Segurança

- **JWT** para autenticação stateless
- **BCrypt** para hash de senhas
- **Rate limiting** para previnir força bruta
- **CORS** configurável por ambiente
- **Senhas e secrets** via variáveis de ambiente

---

## 📝 Licença

MIT License - veja o arquivo LICENSE para detalhes.

---

<div align="center">

**⭐ Se este projeto foi útil, deixe uma estrela!**

*Desenvolvido com ❤️ usando Spring Boot 3*

</div>
