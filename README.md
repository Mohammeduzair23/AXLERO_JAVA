# Axlero Dynamic Data Dashboard Builder

A "Dashboard as a Service" backend. Users sign up, create **workspaces** (tenants), invite teammates with roles,
build **dashboards** made of **widgets** (bar, line, pie, area, KPI), and each widget is powered by a
**dynamic aggregation query** over the workspace's sales data. Results are cached in Redis.

Built for the Axlero Java Development (Spring Boot) internship, Project 1.

## Status

| Week | Scope (from the PRD) | Status |
|---|---|---|
| 1 | Spring Boot setup, layered architecture, JWT auth, entities (User, Workspace, Dashboard, WidgetConfig) | Done |
| 2 | Dynamic query engine with the JPA Criteria API, SQL-injection protection, unit tests | Done |
| 3 | Redis caching (`@EnableCaching`, `@Cacheable`) | Done (backend) |
| 3 | React dashboard with drag-and-drop, Recharts | **Next** |
| 4 | CSV / Excel export (Apache POI), final error handling, Swagger polish, README polish | Planned |

## Tech stack

Java 17, Spring Boot 3.5, Spring Security + JWT (stateless), Spring Data JPA (Hibernate), PostgreSQL,
Spring Cache + Redis, springdoc-openapi (Swagger UI), Lombok, JUnit 5 + Mockito, H2 (tests only).
Frontend (planned): React, Vite, Recharts.

## Architecture

Strict layering. A layer only talks to the layer directly below it.

```
Controller  ->  Service  ->  Repository  ->  Database
 (HTTP, DTOs)   (rules,      (Spring Data
                transactions) JPA only)
```

| Package | Layer | Rule |
|---|---|---|
| `controllers/` | Web | Thin. Validates input (`@Valid`), calls one service method, returns a DTO. No business logic. |
| `services/` | Business | All rules, authorisation checks and `@Transactional` boundaries live here. |
| `repositories/` | Data access | Spring Data interfaces only. No business logic. |
| `entities/` | Domain model | JPA entities with constraints and indexes. **Never returned from an API.** |
| `dto/request`, `dto/response` | DTOs | Java records. Exactly the JSON the client sends or receives. |
| `exceptions/` | Errors | Custom exceptions + one `@RestControllerAdvice` returning a consistent error body. |
| `config/`, `security/` | Infrastructure | Security, OpenAPI, cache configuration, JWT filter. |

### Why DTOs and not entities

Entities describe the database; DTOs describe the API. Keeping them separate means a database change cannot
silently change the API, sensitive fields (like the password hash) can never leak, lazy-loaded relations cannot
blow up during JSON serialisation, and clients cannot set fields they should not (mass assignment).
Services map entities to response records explicitly.

## Getting started

### Prerequisites

- Java 17+, Maven 3.9+
- PostgreSQL (create an empty database, e.g. `dashboard_builder`)
- Redis. Easiest with Docker: `docker run -d --name dash-redis -p 6379:6379 redis:7`

### Configuration (credentials layout)

Secrets live in a git-ignored `.env` file in the project root. Never commit it.

```properties
DB_URL=jdbc:postgresql://localhost:5432/dashboard_builder
DB_USERNAME=postgres
DB_PASSWORD=change-me
JWT_SECRET=at-least-32-characters-long-random-string
# optional
REDIS_HOST=localhost
REDIS_PORT=6379
```

`application.yml` holds shared settings, `application-dev.yml` holds the dev datasource and JWT settings.
In `dev`, Hibernate creates and updates tables from the entities (`ddl-auto: update`).

### Run

```bash
mvn clean test          # unit + query tests (no PostgreSQL or Redis needed)
mvn spring-boot:run     # starts on http://localhost:8080
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

## Security model

- Stateless JWT. Send `Authorization: Bearer <token>` on every call except register, login, Swagger and health.
- **Tenant isolation:** every dashboard, widget and sales record belongs to a workspace, and every query is scoped
  by workspace id.
- **Workspace roles** (checked in one place, `WorkspaceAccessService`):

| Role | Can do |
|---|---|
| VIEWER | Read workspace, members, dashboards, widgets, widget data, run metric queries |
| EDITOR | Everything a viewer can, plus create/update/delete dashboards and widgets, import sales data |
| OWNER | Everything an editor can, plus rename/delete the workspace and add members (one owner per workspace) |

- A user who is **not a member** of a workspace gets **404** (so ids cannot be probed).
  A member with **too low a role** gets **403**.

## API reference

All bodies are JSON. `{ws}`, `{dash}`, `{widget}` are UUIDs.

### Auth (public except `/me`)

| Method | Path | Body | Returns |
|---|---|---|---|
| POST | `/api/auth/register` | `{ "email", "password" (8-72 chars), "fullName" }` | 200 `{ token, user }` |
| POST | `/api/auth/login` | `{ "email", "password" }` | 200 `{ token, user }` |
| GET | `/api/auth/me` | - | 200 `{ id, email, fullName, role }` |

### Workspaces

| Method | Path | Min role | Notes |
|---|---|---|---|
| POST | `/api/workspaces` | any user | Body `{ "name" }`. Caller becomes OWNER. 201 |
| GET | `/api/workspaces` | any user | Workspaces you belong to (with your role) |
| GET | `/api/workspaces/{ws}` | VIEWER | |
| PUT | `/api/workspaces/{ws}` | OWNER | Rename. Body `{ "name" }` |
| DELETE | `/api/workspaces/{ws}` | OWNER | 204. Removes members and dashboards too |
| GET | `/api/workspaces/{ws}/members` | VIEWER | |
| POST | `/api/workspaces/{ws}/members` | OWNER | Body `{ "email", "role": "EDITOR" or "VIEWER" }`. 201 |

### Dashboards

| Method | Path | Min role |
|---|---|---|
| POST | `/api/workspaces/{ws}/dashboards` | EDITOR (body `{ "name", "description" }`, 201) |
| GET | `/api/workspaces/{ws}/dashboards` | VIEWER |
| GET | `/api/workspaces/{ws}/dashboards/{dash}` | VIEWER |
| PUT | `/api/workspaces/{ws}/dashboards/{dash}` | EDITOR |
| DELETE | `/api/workspaces/{ws}/dashboards/{dash}` | EDITOR (204) |

### Widgets

Base path: `/api/workspaces/{ws}/dashboards/{dash}/widgets`

| Method | Path | Min role | Notes |
|---|---|---|---|
| POST | `` | EDITOR | 201 |
| GET | `` | VIEWER | Ordered by `displayOrder` |
| PUT | `/{widget}` | EDITOR | |
| DELETE | `/{widget}` | EDITOR | 204 |
| GET | `/{widget}/data` | VIEWER | Runs the widget's saved query, returns the numbers to draw |

Widget body:

```json
{
  "title": "Revenue by region",
  "metricField": "AMOUNT",
  "aggregationType": "SUM",
  "groupByField": "REGION",
  "chartType": "BAR",
  "displayOrder": 0
}
```

Allowed values: `metricField` = `AMOUNT`, `QUANTITY`; `aggregationType` = `SUM`, `COUNT`, `AVG`, `MIN`, `MAX`;
`groupByField` = `REGION`, `PRODUCT`, `SALE_DATE` (omit for a single KPI value);
`chartType` = `BAR`, `LINE`, `PIE`, `AREA`, `KPI`.

### Sales data

| Method | Path | Min role | Notes |
|---|---|---|---|
| POST | `/api/workspaces/{ws}/sales` | EDITOR | Up to 1000 records per call. 201 `{ "created": n }` |

```json
{
  "records": [
    { "region": "North", "product": "Widget", "amount": 100.00, "quantity": 2, "saleDate": "2026-01-10" }
  ]
}
```

### Metric query (live preview)

`POST /api/workspaces/{ws}/metrics/query` (VIEWER). It is a POST only because the query is a JSON body; it reads data only.

```json
{ "metricField": "AMOUNT", "aggregationType": "SUM", "groupBy": "REGION",
  "dateFrom": "2026-01-01", "dateTo": "2026-03-31" }
```

```json
{ "metricField": "AMOUNT", "aggregationType": "SUM", "groupBy": "REGION",
  "data": [ { "label": "North", "value": 150.00 }, { "label": "South", "value": 200.00 } ] }
```

Without `groupBy` the result is one point labelled `"Total"`. An empty result returns `0`, not null.
At most 200 groups are returned.

### Errors

Every failure uses the same body:

```json
{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "Validation failed",
  "path": "/api/workspaces", "fieldErrors": [ { "field": "name", "message": "must not be blank" } ] }
```

| Status | When |
|---|---|
| 400 | Invalid body, unknown enum value, bad date range |
| 401 | Missing/invalid token, wrong email or password |
| 403 | Member without enough role |
| 404 | Not found, or not a member of that workspace |
| 409 | Duplicate email or member, database constraint violation |

## Dynamic query engine and SQL-injection safety

`MetricQueryExecutor` builds queries with `CriteriaBuilder` / `CriteriaQuery` (no string concatenation anywhere):

1. Column names can only come from the `MetricField` and `GroupByField` enums. Hostile text such as
   `region; DROP TABLE sales_records` cannot even be parsed into a request (covered by tests).
2. Values (workspace id, dates) are bound as parameters by Hibernate.
3. Every query is filtered by workspace inside the engine, so tenant isolation does not depend on callers.

## Caching (Redis)

- `MetricQueryExecutor.execute` is `@Cacheable` (cache `metrics`, key = workspace id + query, TTL 5 minutes).
- The cache is on the engine, not on `MetricService`, so a cache hit can never skip the access check.
- Importing sales data clears the metrics cache (`@CacheEvict`), so dashboards are not stale after an import.
- If Redis is down, the API logs a warning and falls back to the database instead of failing.
- Verify: call a metric endpoint twice. In `dev`, the log line "Metric query executed" appears only on a cache miss.
  Or run `redis-cli KEYS "metrics*"`.

## Testing

`mvn clean test` runs:

- Service unit tests with Mockito (auth, workspaces, dashboards, widgets, sales, access rules).
- Query tests that run the real Criteria queries on an in-memory **H2** database (tests only; the app uses PostgreSQL).
- JSON tests proving malicious input is rejected.
- Cache tests proving a second identical call does not touch the database and that tenants never share a cache entry.

## Git conventions

[Conventional Commits](https://www.conventionalcommits.org/), small and frequent, for example
`feat: add widget data endpoint`, `fix: return 401 for bad credentials`, `test: cover tenant isolation`.

## Roadmap

1. **Week 3, frontend:** React app (Vite) with login, workspace and dashboard screens, a palette of metric and chart types
   to drag onto a canvas, Recharts rendering from the `/data` endpoint.
2. **Week 4:** export dashboard data as CSV / Excel (Apache POI), handle all database constraint violations in
   the global handler, OpenAPI annotations on every endpoint, production profile (`ddl-auto: validate` with a real
   schema), final README polish.

## System architecture

```text
+-----------------------+
| React Frontend        |
| Dashboards and widgets|
+-----------+-----------+
            |
+-----------+-----------+
| Spring Boot API       |
| REST, JWT, access     |
+-----+-------------+---+
      |             |
+-----+-------+  +-----+--------------+
| Redis       |  | PostgreSQL         |
| Metric cache|  | Users, workspaces, |
+------------+   | dashboards, widgets|
                 | and sales data     |
                 +--------------------+
```