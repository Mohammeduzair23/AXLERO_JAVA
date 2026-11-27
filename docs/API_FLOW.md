# End-to-end API flow (Swagger or Postman)

Prerequisites: PostgreSQL and Redis running, app started (`mvn spring-boot:run`).

## Option A: Postman (fastest)

1. Import `docs/dashboard-builder.postman_collection.json`.
2. Open the collection, click **Run**, keep the default order, click **Run Axlero Dashboard Builder**.
3. Every request has assertions (green = pass). Variables such as `token`, `workspaceId`, `dashboardId`, `widgetId`
   are saved automatically, so you can also run requests one by one from folder 1 downward.
4. Folder 9 (cleanup) deletes what you created. Skip it if you want to look at the data in PostgreSQL / Redis.

## Option B: Swagger UI

Open http://localhost:8080/swagger-ui.html. Do the steps in order and copy ids from each response into the next.

### 1. Register, then authorise
`POST /api/auth/register`
```json
{ "email": "olivia@example.com", "password": "Passw0rd!23", "fullName": "Olivia Owner" }
```
Copy the `token` from the response. Click **Authorize** (top right), paste the token (no "Bearer " prefix), Authorize.
From now on Swagger sends it on every call. (`POST /api/auth/login` gives a fresh token later.)

Check it works: `GET /api/auth/me` returns your user.

### 2. Create a workspace
`POST /api/workspaces`
```json
{ "name": "Acme Sales" }
```
Copy `id` as **workspaceId**. You are its OWNER.

### 3. Create a dashboard
`POST /api/workspaces/{workspaceId}/dashboards`
```json
{ "name": "Q1 Sales Overview", "description": "Revenue by region and totals" }
```
Copy `id` as **dashboardId**.

### 4. Import sales data (otherwise widgets have nothing to show)
`POST /api/workspaces/{workspaceId}/sales`
```json
{
  "records": [
    { "region": "North", "product": "Widget", "amount": 100.00, "quantity": 2, "saleDate": "2026-01-10" },
    { "region": "North", "product": "Gadget", "amount": 50.00,  "quantity": 1, "saleDate": "2026-01-15" },
    { "region": "South", "product": "Widget", "amount": 200.00, "quantity": 4, "saleDate": "2026-02-01" },
    { "region": "South", "product": "Gadget", "amount": 75.50,  "quantity": 3, "saleDate": "2026-02-10" },
    { "region": "East",  "product": "Widget", "amount": 120.00, "quantity": 2, "saleDate": "2026-03-05" },
    { "region": "East",  "product": "Gadget", "amount": 60.00,  "quantity": 1, "saleDate": "2026-03-18" }
  ]
}
```
Expect `{ "created": 6 }`.

### 5. Try a live metric query
`POST /api/workspaces/{workspaceId}/metrics/query`
```json
{ "metricField": "AMOUNT", "aggregationType": "SUM", "groupBy": "REGION" }
```
Expect East 180, North 150, South 275.5.

### 6. Create widgets
`POST /api/workspaces/{workspaceId}/dashboards/{dashboardId}/widgets`
```json
{ "title": "Revenue by region", "metricField": "AMOUNT", "aggregationType": "SUM",
  "groupByField": "REGION", "chartType": "BAR", "displayOrder": 0 }
```
```json
{ "title": "Total sales", "metricField": "AMOUNT", "aggregationType": "COUNT",
  "chartType": "KPI", "displayOrder": 1 }
```
Copy the first widget's `id` as **widgetId**.

### 7. Get the numbers a widget should draw
`GET /api/workspaces/{workspaceId}/dashboards/{dashboardId}/widgets/{widgetId}/data`

Call it twice. The first call logs "Metric query executed" in the app console (cache miss). The second does not (served from Redis).

### 8. Teams and roles
1. Register a second user, e.g. `victor@example.com`.
2. As the owner: `POST /api/workspaces/{workspaceId}/members` with `{ "email": "victor@example.com", "role": "VIEWER" }`.
3. Click **Authorize**, log out, paste Victor's token.
4. `GET /api/workspaces/{workspaceId}/dashboards` works (200).
   `POST /api/workspaces/{workspaceId}/dashboards` is refused (**403**: viewers cannot edit).
5. Register a third user who is **not** a member: every call to that workspace returns **404**, not 403.

### 9. Things that should fail
| Try | Expect |
|---|---|
| Any call with no token | 401 |
| Create workspace with `"name": ""` | 400 with `fieldErrors` |
| Metric query with `"groupBy": "region; DROP TABLE sales_records;--"` | 400 |
| Metric query with `"metricField": "password"` | 400 |
| `dateFrom` after `dateTo` | 400 |
| Register the same email twice | 409 |
