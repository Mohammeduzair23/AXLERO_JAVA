# Axlero - Enterprise Data Dashboard Builder

Authenticated users organize **dashboards** inside **workspaces** and place configurable **widgets** on them.

```
User -> Workspace -> Dashboard -> WidgetConfig
```

**Stack:** Java 17 | Spring Boot 3.5.x | Spring Data JPA / Hibernate | PostgreSQL | Spring Security | JWT | Maven
(React + Axios frontend comes in a later week.)

## Week 1 scope

Backend foundation, stateless JWT auth, and the first relational model
(`User`, `Workspace`, `Dashboard`, `WidgetConfig`).

**Out of scope for Week 1:** dashboard editor UI, chart rendering, data-source integrations,
workspace collaboration, fine-grained permissions, refresh tokens, CI/CD.

## Architecture decisions

1. **Workspace ownership:** one owner per workspace in the first release.
2. **Identifiers:** UUID primary keys.
3. **Authentication:** short-lived signed JWT access tokens, stateless Spring Security.
4. **Password storage:** BCrypt hashes only.
5. **Widget flexibility:** PostgreSQL JSONB for type-specific widget settings.
6. **API boundary:** DTOs for requests/responses; JPA entities are never exposed.
7. **Schema ownership:** Hibernate `ddl-auto: update` for local Week 1 only; Flyway migrations + `validate` for shared environments.
8. **Authorization:** current user comes from the security principal; ownership is verified in services.

## Local setup

Prerequisites: JDK 17+, Maven 3.9+, PostgreSQL.

```sql
CREATE DATABASE dashboard_builder;
```

```bash
cp .env.example .env     # then edit DB_USERNAME / DB_PASSWORD
mvn spring-boot:run
```

Verify: `GET http://localhost:8080/api/health` returns `{"status":"UP", ...}`.

## Error format

```json
{
  "timestamp": "2026-11-10T10:15:30Z",
  "status": 404,
  "error": "Not Found",
  "message": "The requested resource was not found.",
  "path": "/api/unknown"
}
```
Validation errors (400) also include a `fieldErrors` array.

## Releases

| Tag  | Content |
|------|---------|
| v1a  | Project setup + PostgreSQL configuration |
| v1b  | Package structure, error handling, health endpoint |
