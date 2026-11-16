# Axlero Dashboard Builder

Axlero is a dashboard-builder backend for organizing dashboards and configurable widgets in workspaces.

**Current status:** the backend foundation and JWT authentication are implemented. The workspace → dashboard → widget data model is in place, but the corresponding management APIs and dashboard functionality are not implemented yet.

## Project progress

| Area | Status | Completed work |
|---|---|---|
| Application and persistence foundation | Complete | Spring Boot application, PostgreSQL/JPA configuration, and local development profile |
| Health and error handling | Complete | `/api/health`, Actuator health endpoints, request validation, and consistent JSON error responses |
| Authentication | Complete | Registration and login, BCrypt password hashing, signed JWT access tokens, stateless security, and current-user endpoint |
| Dashboard data model | Foundation complete | UUID entities for users, workspaces, dashboards, and widget configurations; ownership and parent relationships; JSONB widget configuration |
| Metric configuration | In progress | Metric request model and aggregation types exist; metric execution and data-source integration are not implemented |
| Dashboard/workspace/widget APIs | Not started | No CRUD endpoints or dashboard editing flow yet |
| Frontend and visualizations | Not started | No React UI or chart rendering yet |

The current automated test suite contains a focused unit test for the metric request model; authentication and API integration coverage remain future work.

## Technology

- Java 21
- Spring Boot 3.5.7
- Spring Web, Spring Security, Spring Data JPA, Bean Validation, and Actuator
- PostgreSQL
- JWT (JJWT)
- Maven

## API currently available

| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| `GET` | `/api/health` | Public | Application health response |
| `POST` | `/api/auth/register` | Public | Register a user and return an access token |
| `POST` | `/api/auth/login` | Public | Authenticate a user and return an access token |
| `GET` | `/api/auth/me` | Bearer token | Return the authenticated user's profile |
| `GET` | `/actuator/health` | Public | Actuator health endpoint |

Send the access token on protected requests as `Authorization: Bearer <token>`. Registration and login requests are validated; passwords must be 8–72 characters. Duplicate email addresses return a conflict response.

## Run locally

Prerequisites: JDK 21+, Maven, and PostgreSQL.

Create a database:

```sql
CREATE DATABASE dashboard_db;
```

Create a `.env` file in the project root (do not commit real credentials):

```properties
DB_URL=jdbc:postgresql://localhost:5432/dashboard_db
DB_USERNAME=postgres
DB_PASSWORD=your-local-database-password
JWT_SECRET=replace-with-a-random-secret-at-least-32-characters-long
```

The development profile is active by default. It reads these values from `.env`; `DB_USERNAME` defaults to `postgres` and `JWT_EXPIRATION_MS` defaults to `3600000` (one hour).

Start the application:

```bash
mvn spring-boot:run
```

The server listens on port `8080` by default. Override it with `SERVER_PORT`.

## Error responses

API errors use a consistent JSON shape. Validation failures also include `fieldErrors`.

```json
{
  "timestamp": "2026-11-10T10:15:30Z",
  "status": 404,
  "error": "Not Found",
  "message": "The requested resource was not found.",
  "path": "/api/unknown"
}
```
