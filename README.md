# 📝 Blog Application (Spring Boot 3 + Java 21 + PostgreSQL)

A full-featured Blog platform built with **Spring Boot 3**, **PostgreSQL**, **Spring Security**, and **Thymeleaf**.  
Supports Posts, Comments, Tags, Authentication, Role-based Authorization, and centralized error handling.

---

## 🚀 Features
| Area | Description |
|------|--------------|
| **Authentication** | Session-based login/signup with BCrypt password encoding. |
| **Authorization** | Admins can manage all content; authors can modify their own posts/comments. |
| **Public Access** | Guests can browse posts and add comments (public comment system). |
| **Post Management** | Create, edit, delete posts with tags, pagination, and OR-mode search. |
| **Tag Handling** | Case-insensitive unique tags (`nameLower` enforced). |
| **Comments** | Attached to posts; cascade delete when post removed. |
| **Exception Handling** | Centralized `@ControllerAdvice` for consistent HTML/JSON responses. |

---

## 🧩 Tech Stack
- **Java 21**
- **Spring Boot 3.3+**
- **Spring Data JPA + Hibernate**
- **Spring Security (Session-based)**
- **PostgreSQL**
- **Thymeleaf Templates**

---

## ⚙️ Setup & Run

### 1️⃣ Configure Database
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blogdb
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.error.whitelabel.enabled=false
