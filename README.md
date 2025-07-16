# 💰 Expense Tracker Backend

A RESTful API built with **Spring Boot**, focused on personal expense management. It allows users to register transactions, manage categories and accounts, with JWT authentication and role-based access control (`USER` / `ADMIN`).

> ⚠️ Current version: **backend only** (not fullstack yet).

---

## ⚙️ Tech Stack

* Java 21 + Spring Boot
* Spring Security + JWT
* PostgreSQL
* Redis
* Swagger (Springdoc OpenAPI)
* Docker & Docker Compose
* Lombok
* MapStruct

---

## 🚀 Getting Started

### 1. Prerequisites

* Docker
* Docker Compose

### 2. Clone the repository

```bash
git clone https://github.com/mparreirinha/expensetrackerapp
cd expensetrackerapp
```

### 3. Create the `.env` file

```env
JWT_SECRET=your_secret
JWT_EXPIRATION=your_expiration
POSTGRES_DB=your_db_name
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password
```

### 4. Run the application

```bash
docker-compose up --build
```

### 5. Access the API

* API Base: [http://localhost:8000](http://localhost:8000)
* Swagger UI: [http://localhost:8000/swagger-ui.html](http://localhost:8000/swagger-ui.html)

---

## 📬 Available Endpoints

### 🔐 Auth (`/auth`)

| Method | Endpoint         | Description             |
| ------ | ---------------- | ----------------------- |
| POST   | `/auth/register` | User registration       |
| POST   | `/auth/login`    | Login & token creation  |
| POST   | `/auth/logout`   | Logout & token revoking |

---

### 👤 User Self (`/me`)

| Method | Endpoint              | Description           |
| ------ | --------------------- | --------------------- |
| GET    | `/me`                 | Get current user info |
| POST   | `/me/change-password` | Change password       |
| DELETE | `/me/delete`          | Delete own account    |

---

### 👥 User Admin (`/admin/users`)

| Method | Endpoint            | Description       |
| ------ | ------------------- | ----------------- |
| GET    | `/admin/users`      | List all users    |
| GET    | `/admin/users/{id}` | Get user by ID    |
| DELETE | `/admin/users/{id}` | Delete user by ID |

> ⚠️ Only for users with `Role.ADMIN`

---

### 💸 Transactions (`/transaction`)

| Method | Endpoint               | Description            |
| ------ | ---------------------- | ---------------------- |
| GET    | `/transaction`         | List all transactions  |
| GET    | `/transaction/{id}`    | Get transaction by ID  |
| POST   | `/transaction`         | Create new transaction |
| PUT    | `/transaction/{id}`    | Update transaction     |
| DELETE | `/transaction/{id}`    | Delete transaction     |
| GET    | `/transaction/balance` | Get total balance      |

---

### 📂 Categories (`/category`)

| Method | Endpoint         | Description            |
| ------ | ---------------- | ---------------------- |
| GET    | `/category`      | List user's categories |
| GET    | `/category/{id}` | Get category by ID     |
| POST   | `/category`      | Create new category    |
| PUT    | `/category/{id}` | Update category        |
| DELETE | `/category/{id}` | Delete category        |

---

## 🔐 JWT Authentication

All endpoints (except login and register) require a JWT token in the header:

```http
Authorization: Bearer <your_token>
```

---

## 🧪 Testing with Swagger

1. Open `/swagger-ui.html`
2. Login using `POST /auth/login`
3. Copy the JWT token
4. Click on **Authorize** in Swagger
5. Paste `Bearer <token>` and test the endpoints

---

## 🧑‍💼 Default Admin User

Upon first run, the application creates a default admin user:

| Field    | Value               |
| -------- | ------------------- |
| Username | `admin`             |
| Email    | `admin@example.com` |
| Password | `admin`             |
| Role     | `ADMIN`             |

---

## ✅ Current Status

* ✅ Functional backend with security and auth
* ✅ Ready-to-run with Docker
* ⏳ Frontend not yet implemented