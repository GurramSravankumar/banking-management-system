# 🏦 SecureBank: Enterprise Backend Core

A highly secure, rigid, and concurrent Java Spring Boot backend infrastructure acting as the financial core of the SecureBank ecosystem. Developed to enforce immutable boundaries shielding relational databases dynamically while exposing robust, strictly-typed REST APIs for customer interfaces and administrative portals universally.

<div align="center">
  <img src="https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.5-brightgreen?style=for-the-badge&logo=spring-boot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Spring_Security-Auth-purple?style=for-the-badge&logo=springsecurity" alt="Spring Security" />
  <img src="https://img.shields.io/badge/Database-MySQL-blue?style=for-the-badge&logo=mysql" alt="PostgreSQL" />
</div>

---

## 🌐 Live Configuration

* **Backend Origin:** [Render Cluster API](https://banking-management-system.onrender.com)
* **Frontend Access Interface:** [Launch Application](https://gurramsravankumar.github.io/banking-management-frontend/#/)

---

## 📖 System Overview

Handling sensitive financial payloads requires significantly tighter protocols than standard e-commerce configurations. A Bank Application actively transfers native account constraints and executes physical verifications directly into relational grids.

This system handles advanced financial abstractions directly by mapping strict DTO definitions and enforcing mathematical transaction boundaries. Every single request executed natively traverses the `WebConfig.java` and internal Security Filter Chains designed exclusively to authorize the internal claims held actively inside standard encrypted JWT bearers.

---

## ✨ Features

**Authentication & Security**
* ✅ End-to-end stateless JWT signature validations mitigating explicit Cross-Site Request Forgeries.
* ✅ Active password salting arrays natively handled by B-Crypt encryptions entirely obscuring raw string metrics.
* ✅ `@Transactional` isolation constraints mapping inherently onto transaction endpoints to enforce atomicity.

**Administrative Management**
* ✅ Endpoint execution fetching distinct lists of active unverified users natively.
* ✅ DTO driven `AdminUserUpdateRequestDTO` mapping specific privilege assignments securely isolating admin requests from standard parameter bindings.
* ✅ Deep relational data mutation modifying internal `Account` limitations simultaneously based upon verification booleans universally.

**Customer Business Logics**
* ✅ Secured financial transaction limits blocking overdraft requests structurally.
* ✅ Self-managed account validations tying native `userId` directly from JWT decoding, preventing unauthorized account alterations inherently.

---

## 📸 Technical Overviews

*(Insert database mapping visualizations or API tests mapping endpoints)*

* **Database ER Diagram:** `<!-- Base String Array here -->`
* **Postman API Mappings:** `<!-- Insert URL Here -->`
* **Admin Payload Logics:** `<!-- Insert URL Here -->`

---

## 🛠️ Technology Stack

| Architecture Layer | Core Technology | Purpose |
| :--- | :--- | :--- |
| **Backend Core** | Spring Boot 3 | API routing & Dependency Injection |
| **Data Persistence** | Spring Data JPA | SQL abstraction mapping |
| **Storage Unit** | MySQL / PostgreSQL | Relational Entity storage |
| **Authentication** | Spring Security 6 | HTTP Security Filter Chains |
| **Serialization**| Lombok / Jackson | Boilerplate & Struct array bindings |
| **Deployment Platform**| Render | Containerized application serving |
| **Build Automator** | Maven Wrapper | Dependency pipeline constraints |

---

## 🧠 Core Architecture Patterns

**1. Filter Architectures:**
Network traffic lands directly internally hitting Custom HttpSecurity arrays globally. Unauthorized routes immediately force a Spring generic 403 Forbidden payload structure securely trapping unauthorized metadata.

**2. `@Transactional` Scoping:**
Account transfer methodologies fundamentally rely inherently upon ACID principles. The Business Logic wraps `Account` balance updating structurally alongside `Transaction` trace saving limits enforcing dynamic isolation mechanisms (i.e. if a secondary array fails mapping, the primary balance deduction dynamically executes a pure SQL Rollback ensuring massive database integrity).

---

## 📁 System Blueprint

```text
SpringSecurity/
├── src/main/java/com/sk/SpringSecurity/
│   ├── config/
│   │   ├── DatabaseSeeder.java         # Master test account insertions
│   │   ├── WebConfig.java              # Advanced CORS & Filter mappings
│   │   └── SecurityFilterChain.java    # Universal Security limitations
│   │
│   ├── controller/
│   │   ├── AdminController.java        # Protected overarching Manipulators
│   │   └── TransactionController.java  # Payment & Money exchange gateways
│   │
│   ├── dtos/
│   │   └── AdminUserUpdateRequestDTO.java # Strict object injection limits
│   │
│   ├── entity/ (models)
│   │   ├── User.java                   # Primary account identifiers
│   │   └── Account.java                # Financial property containers
│   │
│   ├── exception/
│   │   └── GlobalExceptionHandler.java # Uniform REST error specifications
│   │
│   └── service/
│       └── ValidationServiceImpl.java  # Database mutation constraints
```

---

## 🗄️ Persistence Models

Configured universally mapping `@Entity` constraints:

* `User`: (Id, FirstName, Role, KycStatus, AuthCredentials). Securing sensitive user identities.
* `Account`: (Id, User_id (fk), Balance, Account_Type). Enforcing real-time mathematical validation limits restricting overloads natively.
* `Transactions`: Mapping complex interactions holding references explicitly pointing identically to multiple Native Accounts actively preventing orphaned mapping structures entirely via Cascade limits. 

---

## 📡 API Implementations

| Method | Access | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| `POST` | ❌ | `/api/auth/login` | Validates standard metrics pushing generic Token signatures statically. |
| `PUT` | 🔴 | `/api/admin/users/{id}` | Administrator modification gateway directly parsing DTO constraints updates natively. |
| `POST` | 🟢 | `/api/accounts/transfer` | Validates JWT owner id limits enforcing active Transaction limitations dynamically mapped. |

*(Auth keys: ❌ Public / 🟢 Valid User / 🔴 Admin Restriction)*

---

## 🖥️ Local Execution

**Prerequisite System Validations:**
1. Git CLI installed
2. Java 21 JDK active mapping internally
3. Compatible MySQL SQL environment

**Initial Checkout Structure:**
```bash
git clone https://github.com/GurramSravankumar/banking-management-system.git
cd banking-management-system
```

**Generate SQL Database Context:**
```sql
CREATE DATABASE banking_db;
```

**Execution Pipeline:**
```bash
./mvnw spring-boot:run
```

---

## 🌐 Environment Overrides

| Property Environment | Description | Required Status | Default Target Matrix |
| :--- | :--- | :--- | :--- |
| `DB_URL` | Application property URL routing | ✅ | `jdbc:mysql://localhost:3306/banking_db` |
| `DB_USERNAME` | Production MySQL Username | ✅ | `root` |
| `DB_PASSWORD` | Production MySQL Password | ✅ | Explicit configuration map |
| `JWT_SECRET` | Active Signature String | ✅ | Fallback mapping bounds |
| `PORT` | Dynamic Render hook | ❌ | `8080` inherently |

---

## 🔮 Roadmap Engineering

* **Two Factor Architectures (2FA):** Implementing TOTP (Time-Based One-Time Password) parameters completely bypassing basic String verification limits generating standard QR interfaces natively internally.
* **Microservice Transition Limits:** Decoupling internal Transaction architectures into specific individual Docker environments routing dynamically universally over Kafka streams preventing massive internal monolithic scaling bottlenecks.
* **Complex Logging Filters:** Injecting strictly formatted ELK Stack (Elasticsearch, Logstash, Kibana) parameter layouts universally enforcing advanced forensic monitoring matrices natively within Spring arrays continuously.

---

## 🧑‍💻 Technical Engineer

**Sravan Kumar Gurram**
* 🏢 **LinkedIn:** [Insert Array Mapping]
* 🐙 **GitHub:** [@GurramSravankumar](https://github.com/GurramSravankumar)

---

## 📄 License Protocols
Universal Apache 2.0 Open Integration Specifications.
