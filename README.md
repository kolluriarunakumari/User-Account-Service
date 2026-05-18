# User Account Microservice

A Spring Boot microservice for managing **User IDs** and **Account Numbers**, with a frontend dashboard and full CloudBees CI/CD pipeline.

---

## Project Structure

```
user-account-service/
├── src/
│   ├── main/java/com/microservice/
│   │   ├── UserAccountServiceApplication.java  ← Entry point
│   │   ├── model/
│   │   │   ├── User.java                        ← User entity
│   │   │   └── Account.java                     ← Account entity
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   └── AccountRepository.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   └── AccountService.java
│   │   ├── controller/
│   │   │   ├── UserController.java              ← REST: /api/users
│   │   │   └── AccountController.java           ← REST: /api/accounts
│   │   └── config/
│   │       └── DataInitializer.java             ← Sample data loader
│   └── main/resources/
│       └── application.properties
├── src/test/java/com/microservice/
│   └── UserServiceTest.java
├── frontend/
│   └── index.html                              ← Frontend dashboard
├── cicd/
│   └── k8s/
│       ├── deployment.yaml                     ← Kubernetes Deployment
│       └── service.yaml                        ← Kubernetes Service
├── .cloudbees/
│   └── workflows/
│       └── ci.yaml                             ← CloudBees CI workflow
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile                                 ← CloudBees Jenkins Pipeline
└── pom.xml
```

---

## Sample Data (auto-loaded on startup)

### Users

| User ID    | Name           | Email                       | Status   |
|------------|----------------|-----------------------------|----------|
| USR-10001  | Alice Johnson  | alice.johnson@example.com   | ACTIVE   |
| USR-10002  | Bob Williams   | bob.williams@example.com    | ACTIVE   |
| USR-10003  | Carol Martinez | carol.martinez@example.com  | ACTIVE   |
| USR-10004  | David Lee      | david.lee@example.com       | INACTIVE |
| USR-10005  | Emma Davis     | emma.davis@example.com      | ACTIVE   |

### Accounts

| Account Number | Type     | Balance      | User ID   | Status |
|----------------|----------|--------------|-----------|--------|
| ACC-20001001   | SAVINGS  | $12,500.00   | USR-10001 | ACTIVE |
| ACC-20001002   | CHECKING | $3,200.50    | USR-10001 | ACTIVE |
| ACC-20002001   | SAVINGS  | $8,750.00    | USR-10002 | ACTIVE |
| ACC-20003001   | PREMIUM  | $55,000.00   | USR-10003 | ACTIVE |
| ACC-20004001   | CHECKING | $150.00      | USR-10004 | FROZEN |
| ACC-20005001   | SAVINGS  | $22,000.00   | USR-10005 | ACTIVE |

---

## Running Locally

### Option 1: Maven (requires JDK 17+)
```bash
mvn spring-boot:run
```

### Option 2: Docker Compose
```bash
docker-compose up --build
```

### Access
- **API**: http://localhost:8080
- **Frontend**: Open `frontend/index.html` in browser
- **H2 Console**: http://localhost:8080/h2-console
- **Health**: http://localhost:8080/actuator/health

---

## REST API Reference

### Users
```
GET    /api/users             → List all users
GET    /api/users/{userId}    → Get user by ID
POST   /api/users             → Create user
PUT    /api/users/{userId}    → Update user
DELETE /api/users/{userId}    → Delete user
```

### Accounts
```
GET    /api/accounts                  → List all accounts
GET    /api/accounts/{accNum}         → Get account by number
GET    /api/accounts/user/{userId}    → Accounts by user
POST   /api/accounts                  → Create account
PUT    /api/accounts/{accNum}         → Update account
DELETE /api/accounts/{accNum}         → Delete account
```

### Example cURL
```bash
# Get all users
curl http://localhost:8080/api/users

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"userId":"USR-10006","fullName":"Frank White","email":"frank@example.com","status":"ACTIVE"}'

# Get accounts for a user
curl http://localhost:8080/api/accounts/user/USR-10001
```

---

## CloudBees CI/CD Setup

### 1. Connect Repository to CloudBees CI
- Log in to CloudBees CI dashboard
- Create new Pipeline job → point to your Git repo
- Set **Script Path** to `Jenkinsfile`

### 2. Configure Credentials in CloudBees
| Credential ID         | Type     | Description                    |
|-----------------------|----------|--------------------------------|
| `docker-registry-creds` | Username/Password | Docker registry login  |
| `kubeconfig-creds`    | Secret File | Kubernetes kubeconfig        |
| `sonar-token`         | Secret Text | SonarQube token              |

### 3. Set Pipeline Variables
| Variable           | Description                            |
|--------------------|----------------------------------------|
| `DOCKER_REGISTRY`  | e.g. `registry.cloudbees.io/your-org`  |

### 4. CloudBees Workflow (ci.yaml)
- Place `.cloudbees/workflows/ci.yaml` at project root
- Triggers on push to `main`, `develop`, and `feature/**` branches

### CI/CD Pipeline Stages

```
Checkout → Build → Unit Tests → Code Quality → Package
    → Docker Build & Push → Deploy Staging → Smoke Test
    → (manual gate) → Deploy Production
```

---

## Tech Stack

| Layer      | Technology                    |
|------------|-------------------------------|
| Language   | Java 17                       |
| Framework  | Spring Boot 3.2               |
| Database   | H2 (in-memory, swap for prod) |
| ORM        | Spring Data JPA / Hibernate   |
| Build      | Maven 3.9                     |
| Container  | Docker + Alpine JRE           |
| Orchestration | Kubernetes                 |
| CI/CD      | CloudBees CI (Jenkins-based)  |
| Frontend   | Plain HTML/CSS/JS             |
