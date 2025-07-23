# 💰 Expense Tracker Backend

A RESTful API built with **Spring Boot**, focused on personal expense management. It allows users to register transactions, manage categories and accounts, with JWT authentication and role-based access control (`USER` / `ADMIN`).

> ⚠️ Current version: **backend only** (not fullstack yet).

---

## ⚙️ Tech Stack

* Java 17 + Spring Boot
* Spring Security + JWT + Keycloak
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

### 3. Configure environment variables

Copy `.env.example` to `.env` and fill in the values for your environment. **Important:** The `KEYCLOAK_CLIENT_SECRET` must be filled after importing the realm in Keycloak (see below).

```bash
cp .env.example .env
# Edit the .env file and fill in the required values
```

### 4. Import the Keycloak realm

Use the file `keycloak/realm-example.json` to import the realm configuration into Keycloak. **This file does not contain sensitive secrets.**

After importing, go to the Keycloak admin panel, navigate to **Clients > expensetracker-backend > Credentials**, and copy the generated client secret. Paste this value into `KEYCLOAK_CLIENT_SECRET` in your `.env` file.

> **Never commit `realm-export.json` with real secrets to Git!**

### 5. Run the application

```bash
docker-compose up --build
```

### 6. Access the API

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

If you want a default admin user, create it manually in Keycloak after importing the realm, or export a realm with users for development only (never for production).

---

## ✅ Current Status

* ✅ Functional backend with security and auth
* ✅ Ready-to-run with Docker
* ⏳ Frontend not yet implemented