# EduFlow — Learning Management System

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat&logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-316192?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?style=flat&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-Deployed-FF9900?style=flat&logo=amazonaws&logoColor=white)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-D24939?style=flat&logo=jenkins&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Driven-231F20?style=flat&logo=apachekafka&logoColor=white)

A production-ready **Learning Management System** backend built with Spring Boot 4, deployed on AWS with a complete CI/CD pipeline. Features JWT authentication, Google OAuth2, event-driven notifications via Kafka, and role-based access control.

---

## 🌐 Live URLs

| Service | URL |
|---------|-----|
| **Backend API** | `https://d35qzhxh4jwgyd.cloudfront.net` |
| **Frontend** | `https://d3li9dplflwnfz.cloudfront.net` |

---

## 🏗️ System Architecture

```
Users / Clients
      ↓
CloudFront (HTTPS) ──→ S3 (React Frontend)
      ↓
CloudFront API Proxy (HTTPS → HTTP)
      ↓
EC2 (Docker · Spring Boot · Port 8080)
      ↓                    ↓
RDS PostgreSQL          AWS S3
(Private Subnet)      (File Storage)
      ↓
Apache Kafka (Event-Driven Notifications)
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security · JWT · BCrypt · Google OAuth2 |
| Database | PostgreSQL 16 (AWS RDS) |
| Migrations | Flyway |
| Messaging | Apache Kafka (KRaft mode) |
| File Storage | AWS S3 |
| Containerization | Docker (multi-stage builds) |
| CI/CD | Jenkins Pipeline |
| Cloud | AWS EC2 · RDS · S3 · CloudFront · VPC |
| Build Tool | Maven |

---

## ✨ Features

- 🔐 JWT Authentication + Google OAuth2 Social Login
- 👥 Role-based access control (STUDENT / INSTRUCTOR)
- 📚 Course management with search, pagination, category & level filtering
- 📋 Enrollment system with duplicate prevention
- ⭐ Reviews & ratings (enrolled students only)
- 🔔 Event-driven notifications via Apache Kafka
- 📁 File uploads (profile pictures, course thumbnails) to AWS S3
- 🗄️ Database versioning with Flyway migrations
- 🐳 Dockerized with multi-stage builds
- 🚀 Automated CI/CD — git push → auto deploy

---

## 🚀 Getting Started (Local)

### Prerequisites
- Java 21
- Maven 3.8+
- PostgreSQL 16
- Apache Kafka
- Docker (optional)

### 1. Clone the repository
```bash
git clone https://github.com/Akshay269/Eduflow-Spring-Boot.git
cd Eduflow-Spring-Boot
```

### 2. Configure environment
Create `src/main/resources/application-local.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/eduflow_db
    username: postgres
    password: your_password
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your_google_client_id
            client-secret: your_google_client_secret

jwt:
  secret: your_jwt_secret_key

aws:
  access-key: your_aws_access_key
  secret-key: your_aws_secret_key
  bucket-name: your_bucket_name
  region: ap-south-1
```

### 3. Run the application
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 4. Run with Docker
```bash
docker-compose up -d
```

---

## 📡 API Reference

**Base URL:** `https://d35qzhxh4jwgyd.cloudfront.net`

> 🔓 = Public (no token required)
> 🔐 = Protected (Bearer token required)
> Add header: `Authorization: Bearer <token>`

---

### 🔑 Authentication

#### Register
```
POST /api/auth/register 🔓
```
**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "Password@123",
  "role": "STUDENT"
}
```
> `role` accepts: `STUDENT` or `INSTRUCTOR`

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "STUDENT"
  }
}
```

---

#### Login
```
POST /api/auth/login 🔓
```
**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "Password@123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "STUDENT"
  }
}
```

---

#### Google OAuth2 Login
```
GET /oauth2/authorization/google 🔓
```
> Redirects to Google login. On success, redirects to frontend with token:
> `https://frontend.com/oauth2/callback?token=<jwt_token>`

---

### 📚 Courses

#### Get All Published Courses (Paginated)
```
GET /api/courses 🔓
```
**Query Parameters:**
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number |
| `size` | int | 10 | Items per page |
| `sortBy` | string | createdAt | Sort field |

**Example:**
```
GET /api/courses?page=0&size=6&sortBy=createdAt
```
**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Spring Boot Masterclass",
      "description": "Complete guide...",
      "price": 49.99,
      "category": "Programming",
      "level": "INTERMEDIATE",
      "averageRating": 4.67,
      "totalReviews": 3,
      "thumbnailUrl": null,
      "published": true,
      "instructorName": "John Smith",
      "createdAt": "2026-03-08T08:22:33"
    }
  ],
  "pageNumber": 0,
  "pageSize": 6,
  "totalElements": 10,
  "totalPages": 2,
  "first": true,
  "last": false
}
```

---

#### Search Courses
```
GET /api/courses/search 🔓
```
**Query Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `keyword` | string | No | Search in title/description |
| `category` | string | No | `Programming` · `DevOps` · `Data Science` |
| `level` | string | No | `BEGINNER` · `INTERMEDIATE` · `ADVANCED` |
| `page` | int | No | Default: 0 |
| `size` | int | No | Default: 10 |

**Examples:**
```
GET /api/courses/search?keyword=spring
GET /api/courses/search?category=DevOps&level=BEGINNER
GET /api/courses/search?keyword=docker&category=DevOps&page=0&size=5
```

---

#### Get Course by ID
```
GET /api/courses/{id} 🔓
```
**Example:** `GET /api/courses/1`

---

#### Create Course
```
POST /api/courses 🔐 (INSTRUCTOR only)
```
**Request Body:**
```json
{
  "title": "My New Course",
  "description": "Course description here",
  "price": 49.99,
  "category": "Programming",
  "level": "BEGINNER"
}
```
> `level` accepts: `BEGINNER` · `INTERMEDIATE` · `ADVANCED`

---

#### Update Course
```
PUT /api/courses/{id} 🔐 (INSTRUCTOR only)
```
**Request Body:** Same as Create Course

---

#### Delete Course
```
DELETE /api/courses/{id} 🔐 (INSTRUCTOR only)
```

---

#### Upload Course Thumbnail
```
POST /api/courses/{id}/thumbnail 🔐 (INSTRUCTOR only)
```
**Request:** `multipart/form-data`
| Field | Type | Description |
|-------|------|-------------|
| `file` | File | Image file (jpg, png) |

---

#### Get My Courses
```
GET /api/courses/my-courses 🔐 (INSTRUCTOR only)
```

---

### 📋 Enrollments

#### Enroll in Course
```
POST /api/enrollments/course/{courseId} 🔐 (STUDENT only)
```
**No request body required.**

**Example:** `POST /api/enrollments/course/1`

**Response:**
```json
{
  "id": 1,
  "courseId": 1,
  "studentId": 4,
  "enrolledAt": "2026-03-08T08:22:33",
  "courseName": "Spring Boot Masterclass",
  "instructorName": "John Smith"
}
```

---

#### Get My Enrollments
```
GET /api/enrollments/my-enrollments 🔐 (STUDENT only)
```

---

#### Check Enrollment Status
```
GET /api/enrollments/check/{courseId} 🔓
```
**Example:** `GET /api/enrollments/check/1`

**Response:**
```json
true
```

---

#### Get Students Enrolled in Course
```
GET /api/enrollments/course/{courseId}/students 🔐 (INSTRUCTOR only)
```

---

### ⭐ Reviews

#### Get Course Reviews
```
GET /api/reviews/course/{courseId} 🔓
```
**Query Parameters:**
| Parameter | Type | Default |
|-----------|------|---------|
| `page` | int | 0 |
| `size` | int | 10 |

**Example:** `GET /api/reviews/course/1?page=0&size=5`

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "courseId": 1,
      "studentId": 4,
      "studentName": "Alice Brown",
      "rating": 5,
      "comment": "Absolutely brilliant course!",
      "createdAt": "2026-03-08T08:22:33",
      "updatedAt": null
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 3,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

---

#### Create Review
```
POST /api/reviews/course/{courseId} 🔐 (STUDENT only · must be enrolled)
```
**Request Body:**
```json
{
  "rating": 5,
  "comment": "Amazing course, highly recommended!"
}
```
> `rating` accepts: `1` to `5`

---

#### Update Review
```
PUT /api/reviews/{reviewId} 🔐 (STUDENT only · own review)
```
**Request Body:**
```json
{
  "rating": 4,
  "comment": "Updated review comment"
}
```

---

#### Delete Review
```
DELETE /api/reviews/{reviewId} 🔐 (STUDENT only · own review)
```

---

#### Get My Review for a Course
```
GET /api/reviews/course/{courseId}/my-review 🔐 (STUDENT only)
```

---

### 👤 User Profile

#### Get Profile
```
GET /api/users/profile 🔐
```
**Response:**
```json
{
  "id": 4,
  "name": "Alice Brown",
  "email": "alice.student@eduflow.com",
  "role": "STUDENT",
  "profilePictureUrl": null
}
```

---

#### Update Profile
```
PUT /api/users/profile 🔐
```
**Request Body:**
```json
{
  "name": "Alice Updated"
}
```

---

#### Upload Profile Picture
```
POST /api/users/profile/picture 🔐
```
**Request:** `multipart/form-data`
| Field | Type | Description |
|-------|------|-------------|
| `file` | File | Image file (jpg, png) |

---

### 🏥 Health Check

```
GET /actuator/health 🔓
```
**Response:**
```json
{
  "status": "UP"
}
```

---

## 🧪 Test Credentials

Use these to test the API without registering:

### 👨‍🏫 Instructors
| Name | Email | Password |
|------|-------|----------|
| John Smith | john.instructor@eduflow.com | Instructor@123 |
| Sarah Johnson | sarah.instructor@eduflow.com | Instructor@123 |
| Mike Chen | mike.instructor@eduflow.com | Instructor@123 |

### 👨‍🎓 Students
| Name | Email | Password |
|------|-------|----------|
| Alice Brown | alice.student@eduflow.com | Student@123 |
| Bob Wilson | bob.student@eduflow.com | Student@123 |
| Carol Davis | carol.student@eduflow.com | Student@123 |

---

## 🔄 CI/CD Pipeline

Every `git push` to `master` automatically:

```
git push → Jenkins Webhook → mvn build → Docker Build
→ Push to Docker Hub (z9shay/eduflow) → SSH Deploy to EC2 → Live ✅
```

**Jenkins Pipeline Stages:**
1. Checkout — pulls latest code from GitHub
2. Build — `mvn clean package -DskipTests`
3. Test — skipped (no test DB on CI server)
4. Docker Build — multi-stage build
5. Docker Push — pushes to Docker Hub
6. Deploy — SSH into EC2, pulls image, restarts container with env-file

---

## ☁️ AWS Infrastructure

```
VPC: eduflow-vpc (10.0.0.0/16) — ap-south-1

Public Subnet (10.0.1.0/24):
  └── EC2 c7i-flex.large (Jenkins + Docker + App)

Private Subnets (10.0.2.0/24, 10.0.3.0/24):
  └── RDS PostgreSQL 16 (db.t3.micro)

CloudFront:
  ├── Frontend CDN → S3 (React App)
  └── API Proxy → EC2:8080 (HTTPS termination)

Security Groups:
  ├── jenkins-sg: ports 22, 8080, 9090
  └── rds-sg: port 5432 ← jenkins-sg only

IAM: jenkins-ec2-role (S3 + ECR + SecretsManager)
Elastic IP: assigned to EC2 (permanent IP)
```

---

## 📁 Project Structure

```
src/main/java/com/eduflow/eduflow/
├── auth/
│   ├── AuthController.java
│   ├── JwtAuthFilter.java
│   ├── JwtService.java
│   ├── OAuth2SuccessHandler.java
│   └── OAuth2UserService.java
├── common/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── CorsConfig.java
│   │   └── KafkaConfig.java
│   └── response/
│       └── PageResponse.java
├── course/
│   ├── Course.java
│   ├── CourseController.java
│   ├── CourseRepository.java
│   └── CourseService.java
├── enrollment/
│   ├── Enrollment.java
│   ├── EnrollmentController.java
│   ├── EnrollmentRepository.java
│   └── EnrollmentService.java
├── review/
│   ├── Review.java
│   ├── ReviewController.java
│   ├── ReviewRepository.java
│   └── ReviewService.java
├── user/
│   ├── User.java
│   ├── UserController.java
│   ├── UserRepository.java
│   └── UserService.java
└── notification/
    ├── Notification.java
    ├── NotificationService.java
    └── NotificationConsumer.java

src/main/resources/
├── application.yml
└── db/migration/
    ├── V1__create_users_table.sql
    ├── V2__create_courses_table.sql
    ├── V3__create_enrollments_table.sql
    ├── V4__create_notifications_table.sql
    ├── V5__create_sections_lessons.sql
    ├── V6__add_course_fields.sql
    └── V7__create_reviews_table.sql

Dockerfile
docker-compose.yml
Jenkinsfile
```

---

## 🐛 Key Debugging Solved

| Issue | Fix |
|-------|-----|
| Jenkins Groovy `MissingPropertyException` | Used `withCredentials` + env-file approach |
| Docker container hitting `localhost` instead of EC2 | Used Docker bridge gateway `172.17.0.1` |
| Port 8080 conflict (Jenkins vs App) | Moved Jenkins to port 9090 |
| Flyway checksum mismatch | Deleted row from `flyway_schema_history` |
| RDS subnet group needs 2 AZs | Added second private subnet in ap-south-1b |
| Mixed Content (HTTPS → HTTP) | Added CloudFront as HTTPS proxy for backend |
| `redirect_uri_mismatch` Google OAuth | Added CloudFront URL to Google Console authorized URIs |


## 🤝 Connect

**Akshay Rahangdale**
- GitHub: [@Akshay269](https://github.com/Akshay269)
- LinkedIn: [Akshay Rahangdale](https://www.linkedin.com/in/akshayr2609/)
