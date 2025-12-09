# Anki Server - Developer Guide

> **A comprehensive guide for Node.js developers working with this Java Spring Boot application**

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Database Schema](#database-schema)
4. [Getting Started](#getting-started)
5. [API Testing with Bruno](#api-testing-with-bruno)

---

## 🎯 Overview

### What This Application Does

The **Anki Server** is a RESTful API backend for managing flashcard-based learning (Anki cards). It provides:

- **User Management**: Role-based authentication (Teachers & Students)
- **Card Management**: CRUD operations for Anki flashcards with difficulty levels
- **Card Assignment**: Teachers can assign specific cards to students
- **Learning History**: Track student progress and review patterns
- **JWT Authentication**: Secure token-based authentication

### Key Features

- 🔐 **JWT-based Authentication** with role-based access control (RBAC)
- 👨‍🏫 **Teacher Role**: Create cards, assign cards to students, view all cards
- 👨‍🎓 **Student Role**: View assigned cards, track learning history
- 📊 **Learning Analytics**: Review history with ratings and timestamps
- 🗃️ **PostgreSQL Database**: Relational data storage with JPA/Hibernate ORM

### Tech Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.4.0 |
| **Language** | Java 21 |
| **Database** | PostgreSQL 15 |
| **ORM** | Hibernate/JPA |
| **Security** | Spring Security + JWT |
| **Build Tool** | Maven |
| **Container** | Docker (PostgreSQL) |

---

## 🏗️ Architecture

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                             │
│                    (Bruno/Postman/Frontend)                      │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTP/REST
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Application                     │
│                                                                   │
│  ┌──────────────┐      ┌──────────────┐      ┌──────────────┐  │
│  │ Controllers  │◄────►│  Services    │◄────►│ Repositories │  │
│  │              │      │              │      │              │  │
│  │ - Auth       │      │ - Auth       │      │ - User       │  │
│  │ - Card       │      │ - Card       │      │ - Card       │  │
│  │ - History    │      │ - History    │      │ - History    │  │
│  └──────┬───────┘      └──────────────┘      └──────┬───────┘  │
│         │                                             │          │
│  ┌──────▼──────────────────────────────────────────┐ │          │
│  │        Security Layer (JWT Filter)              │ │          │
│  │  - AuthTokenFilter                              │ │          │
│  │  - JwtUtils                                     │ │          │
│  │  - CustomUserDetailsService                     │ │          │
│  └─────────────────────────────────────────────────┘ │          │
│                                                       │          │
│  ┌────────────────────────────────────────────────────▼───────┐ │
│  │                    Entity Layer                            │ │
│  │  - User (Teacher/Student)                                  │ │
│  │  - AnkiCard (Word, Pronunciation, Definition, Difficulty)  │ │
│  │  - CardAssignment (Teacher assigns card to Student)        │ │
│  │  - LearningHistory (Student review records)                │ │
│  └────────────────────────────────────────────────────────────┘ │
└────────────────────────────┬────────────────────────────────────┘
                             │ JDBC
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PostgreSQL Database                           │
│                      (Docker Container)                          │
└─────────────────────────────────────────────────────────────────┘
```

### Component Breakdown

#### 1. **Controller Layer** (`/controller`)
- **AuthController**: Handles user registration and login
- **CardController**: CRUD operations for Anki cards, card assignment
- **HistoryController**: Learning history tracking

#### 2. **Service Layer** (`/service`)
- Business logic and data transformation
- Interfaces between controllers and repositories

#### 3. **Repository Layer** (`/repository`)
- JPA repositories for database operations
- Custom queries using Spring Data JPA

#### 4. **Security Layer** (`/security`)
- **AuthTokenFilter**: Intercepts requests, validates JWT tokens
- **JwtUtils**: Token generation and validation
- **CustomUserDetailsService**: Loads user details for authentication
- **SecurityConfig**: Configures security rules and filters

#### 5. **Entity Layer** (`/entity`)
- JPA entities mapping to database tables
- Relationships: User ↔ CardAssignment ↔ AnkiCard, User ↔ LearningHistory ↔ AnkiCard

---

## 🗄️ Database Schema

### Entity Relationship Diagram

```
┌─────────────────────┐
│       User          │
├─────────────────────┤
│ id (PK)             │
│ username (unique)   │
│ password (hashed)   │
│ email               │
│ fullName            │
│ role (ENUM)         │◄────┐
└──────┬──────────────┘     │
       │                    │
       │ 1                  │
       │                    │
       │ N                  │
       ▼                    │
┌─────────────────────┐     │
│  LearningHistory    │     │
├─────────────────────┤     │
│ id (PK)             │     │
│ student_id (FK) ────┼─────┘
│ card_id (FK) ───────┼───┐
│ reviewDate          │   │
│ rating (ENUM)       │   │
└─────────────────────┘   │
                          │
       ┌──────────────────┘
       │
       │ N
       │
       │ 1
       ▼
┌─────────────────────┐
│     AnkiCard        │
├─────────────────────┤
│ id (PK)             │
│ word                │
│ pronunciation       │
│ definition          │
│ difficulty (ENUM)   │◄────┐
└──────┬──────────────┘     │
       │                    │
       │ 1                  │
       │                    │
       │ N                  │
       ▼                    │
┌─────────────────────┐     │
│  CardAssignment     │     │
├─────────────────────┤     │
│ id (PK)             │     │
│ card_id (FK) ───────┼─────┘
│ student_id (FK) ────┼───┐
│ teacher_id (FK) ────┼───┼─┐
│ assignedDate        │   │ │
└─────────────────────┘   │ │
                          │ │
       ┌──────────────────┘ │
       │                    │
       │ N                  │ N
       │                    │
       │ 1                  │ 1
       ▼                    ▼
┌─────────────────────┐ ┌─────────────────────┐
│  User (Student)     │ │  User (Teacher)     │
└─────────────────────┘ └─────────────────────┘
```

### Tables Overview

#### **users**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| username | VARCHAR(50) | Unique username |
| password | VARCHAR(255) | BCrypt hashed password |
| email | VARCHAR(100) | User email |
| full_name | VARCHAR(100) | Display name |
| role | VARCHAR(20) | ROLE_TEACHER or ROLE_STUDENT |

#### **anki_cards**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| word | VARCHAR(100) | Vocabulary word |
| pronunciation | VARCHAR(100) | IPA pronunciation |
| definition | TEXT | Word definition |
| difficulty | VARCHAR(20) | EASY, MEDIUM, or HARD |

#### **card_assignments**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| card_id | BIGINT | Foreign key to anki_cards |
| student_id | BIGINT | Foreign key to users (student) |
| teacher_id | BIGINT | Foreign key to users (teacher) |
| assigned_date | TIMESTAMP | When card was assigned |

#### **learning_history**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| student_id | BIGINT | Foreign key to users (student) |
| card_id | BIGINT | Foreign key to anki_cards |
| review_date | TIMESTAMP | When card was reviewed |
| rating | VARCHAR(20) | Student's self-rating (EASY/MEDIUM/HARD) |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker** and **Docker Compose**
- **Bruno** (API client) or Postman

### 1. Start Docker (PostgreSQL)

```bash
# Navigate to project root
cd /path/to/anki-server-java

# Start PostgreSQL container
docker-compose up -d

# Verify container is running
docker ps | grep anki_postgres
```

**Docker Configuration** (`docker-compose.yml`):
```yaml
services:
  db:
    image: postgres:15
    container_name: anki_postgres
    environment:
      POSTGRES_DB: anki_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
```

### 2. Start the Application

```bash
# Using Maven wrapper (recommended)
./mvnw spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

**Application Properties** (`src/main/resources/application.properties`):
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/anki_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000  # 24 hours in milliseconds

# Server
server.port=8080
```

### 3. Database Initialization

The application uses **automatic seeding** via `DataSeeder.java`:

#### What Gets Seeded:
- **2 Teachers**: `teacher1`, `teacher2` (password: `123456`)
- **10 Students**: `student1_t1` through `student5_t1`, `student1_t2` through `student5_t2` (password: `123456`)
- **20 Anki Cards**: Various vocabulary words with pronunciations and definitions
- **Random Learning History**: 5 reviews per student

#### Re-Initialize Database (Fresh Start)

**Option 1: Drop and Recreate Database**
```bash
# Stop the application
# Ctrl+C or kill the process

# Connect to PostgreSQL
docker exec -it anki_postgres psql -U postgres

# Drop and recreate database
DROP DATABASE anki_db;
CREATE DATABASE anki_db;
\q

# Restart application (will auto-seed)
./mvnw spring-boot:run
```

**Option 2: Delete All Data (Keep Schema)**
```bash
# Connect to database
docker exec -it anki_postgres psql -U postgres -d anki_db

# Delete all data
TRUNCATE TABLE learning_history CASCADE;
TRUNCATE TABLE card_assignments CASCADE;
TRUNCATE TABLE anki_cards CASCADE;
TRUNCATE TABLE users CASCADE;
\q

# Restart application (will auto-seed)
./mvnw spring-boot:run
```

**Option 3: Nuclear Option (Complete Reset)**
```bash
# Stop application and remove container
docker-compose down -v

# Start fresh
docker-compose up -d
./mvnw spring-boot:run
```

---

## 🧪 API Testing with Bruno

### Setup Bruno

1. **Install Bruno**: Download from [usebruno.com](https://www.usebruno.com/)
2. **Open Collection**: File → Open Collection → Select `/Bruno` folder
3. **Environment**: The collection uses `{{base_url}}` variable (default: `http://localhost:8080`)

### Basic Sample Flow

#### **Step 1: Sign In as Teacher**

**Request**: `POST /api/auth/signin`
```json
{
  "username": "teacher1",
  "password": "123456"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "teacher1",
  "email": "teacher1@anki.com",
  "role": "ROLE_TEACHER"
}
```

**Bruno File**: `Bruno/Auth/Sign In (Teacher).bru`
- The token is automatically saved to `{{token}}` variable via post-response script

---

#### **Step 2: Get All Cards (Authenticated)**

**Request**: `GET /api/cards`
**Headers**: `Authorization: Bearer {{token}}`

**Response**:
```json
[
  {
    "id": 1,
    "word": "Epiphany",
    "pronunciation": "/əˈpifənē/",
    "definition": "A moment of sudden revelation or insight",
    "difficulty": "HARD"
  },
  {
    "id": 2,
    "word": "Serendipity",
    "pronunciation": "/ˌserənˈdipədē/",
    "definition": "The occurrence of events by chance in a happy way",
    "difficulty": "MEDIUM"
  }
  // ... more cards
]
```

**Bruno File**: `Bruno/Cards/Get All Cards.bru`

---

#### **Step 3: Create New Card (Teacher Only)**

**Request**: `POST /api/cards`
**Headers**: `Authorization: Bearer {{token}}`
```json
{
  "word": "Eloquent",
  "pronunciation": "/ˈeləkwənt/",
  "definition": "Fluent or persuasive in speaking or writing",
  "difficulty": "MEDIUM"
}
```

**Response**:
```json
{
  "id": 21,
  "word": "Eloquent",
  "pronunciation": "/ˈeləkwənt/",
  "definition": "Fluent or persuasive in speaking or writing",
  "difficulty": "MEDIUM"
}
```

**Bruno File**: `Bruno/Cards/Create Card (Teacher Only).bru`

---

#### **Step 4: Assign Card to Student (Teacher Only)**

**Request**: `POST /api/cards/assign`
**Headers**: `Authorization: Bearer {{token}}`
```json
{
  "cardId": 1,
  "studentId": 3
}
```

**Response**:
```json
{
  "id": 1,
  "card": {
    "id": 1,
    "word": "Epiphany",
    "pronunciation": "/əˈpifənē/",
    "definition": "A moment of sudden revelation or insight",
    "difficulty": "HARD"
  },
  "student": {
    "id": 3,
    "username": "student1_t1",
    "email": "s1t1@anki.com",
    "fullName": "Student 1 of T1",
    "role": "ROLE_STUDENT"
  },
  "teacher": {
    "id": 1,
    "username": "teacher1"
  },
  "assignedDate": "2025-12-09T12:30:00"
}
```

**Bruno File**: `Bruno/Cards/Assign Card (Teacher Only).bru`

---

#### **Step 5: Sign In as Student**

**Request**: `POST /api/auth/signin`
```json
{
  "username": "student1_t1",
  "password": "123456"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 3,
  "username": "student1_t1",
  "email": "s1t1@anki.com",
  "role": "ROLE_STUDENT"
}
```

**Bruno File**: `Bruno/Auth/Sign In (Student).bru`

---

#### **Step 6: Get Assigned Cards (Student)**

**Request**: `GET /api/cards/assigned`
**Headers**: `Authorization: Bearer {{token}}`

**Response**:
```json
[
  {
    "id": 1,
    "word": "Epiphany",
    "pronunciation": "/əˈpifənē/",
    "definition": "A moment of sudden revelation or insight",
    "difficulty": "HARD"
  }
  // ... other assigned cards
]
```

**Bruno File**: `Bruno/Cards/Get Assigned Cards (Student).bru`

---

#### **Step 7: Record Learning History (Student)**

**Request**: `POST /api/history`
**Headers**: `Authorization: Bearer {{token}}`
```json
{
  "cardId": 1,
  "rating": "MEDIUM"
}
```

**Response**:
```json
{
  "id": 51,
  "student": {
    "id": 3,
    "username": "student1_t1"
  },
  "card": {
    "id": 1,
    "word": "Epiphany"
  },
  "reviewDate": "2025-12-09T12:35:00",
  "rating": "MEDIUM"
}
```

**Bruno File**: `Bruno/History/Record Review.bru`

---

#### **Step 8: Get Student's Learning History**

**Request**: `GET /api/history/student/{studentId}`
**Headers**: `Authorization: Bearer {{token}}`

**Response**:
```json
[
  {
    "id": 51,
    "card": {
      "id": 1,
      "word": "Epiphany",
      "difficulty": "HARD"
    },
    "reviewDate": "2025-12-09T12:35:00",
    "rating": "MEDIUM"
  }
  // ... more history records
]
```

**Bruno File**: `Bruno/History/Get Student History.bru`

---

### Complete Bruno Workflow Summary

```
1. Sign In (Teacher)          → Get JWT token
2. Get All Cards              → View all available cards
3. Create New Card            → Add vocabulary to system
4. Assign Card to Student     → Teacher assigns card
5. Sign In (Student)          → Get student JWT token
6. Get Assigned Cards         → Student views their cards
7. Record Learning History    → Student logs review
8. Get Learning History       → View progress over time
```

---

## 📚 Additional Resources

### API Endpoints Quick Reference

| Endpoint | Method | Auth | Role | Description |
|----------|--------|------|------|-------------|
| `/api/auth/signup` | POST | ❌ | - | Register new user |
| `/api/auth/signin` | POST | ❌ | - | Login and get JWT |
| `/api/cards` | GET | ✅ | All | Get all cards |
| `/api/cards` | POST | ✅ | Teacher | Create new card |
| `/api/cards/assign` | POST | ✅ | Teacher | Assign card to student |
| `/api/cards/assigned` | GET | ✅ | Student | Get assigned cards |
| `/api/history` | POST | ✅ | Student | Record review |
| `/api/history/student/{id}` | GET | ✅ | All | Get student history |

### Default Credentials

| Username | Password | Role |
|----------|----------|------|
| teacher1 | 123456 | ROLE_TEACHER |
| teacher2 | 123456 | ROLE_TEACHER |
| student1_t1 | 123456 | ROLE_STUDENT |
| student2_t1 | 123456 | ROLE_STUDENT |
| ... | ... | ... |

### Troubleshooting

**Issue**: `401 Unauthorized` on protected endpoints
- **Solution**: Ensure JWT token is included in `Authorization: Bearer <token>` header

**Issue**: Database connection refused
- **Solution**: Check if Docker container is running: `docker ps | grep anki_postgres`

**Issue**: Port 8080 already in use
- **Solution**: Change `server.port` in `application.properties` or kill process on port 8080

**Issue**: Data not seeding
- **Solution**: Database already has data. Use re-initialization steps above.

---

## 🎓 For Node.js Developers

### Java ↔ Node.js Equivalents

| Java/Spring | Node.js Equivalent |
|-------------|-------------------|
| `@RestController` | Express router |
| `@Service` | Service class/module |
| `@Repository` | Database model/repository |
| `@Entity` | Sequelize/TypeORM model |
| `@Autowired` | Dependency injection |
| `application.properties` | `.env` file |
| Maven (`pom.xml`) | npm (`package.json`) |
| JPA/Hibernate | Sequelize/TypeORM/Prisma |
| BCryptPasswordEncoder | bcrypt library |
| JWT (jjwt) | jsonwebtoken library |

### Project Structure Comparison

```
Java Spring Boot              Node.js Express
├── controller/              ├── routes/
├── service/                 ├── services/
├── repository/              ├── models/
├── entity/                  ├── models/
├── security/                ├── middleware/
├── dto/                     ├── types/ or interfaces/
└── config/                  └── config/
```

---

**Happy Coding! 🚀**
