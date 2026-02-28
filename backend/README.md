# Splitwise (Backend)

A lightweight Splitwise-like backend service implemented with Spring Boot. It provides user management, groups, expense sharing, settlements, JWT-based authentication and Google OAuth login.

This README covers how to configure, build, run and test the project locally.

## Table of Contents

- Project Overview
- Tech Stack
- Prerequisites
- Configuration
- Build & Run (Windows)
- Environment variables
- API - common endpoints and examples
- Database
- Tests
- Troubleshooting
- Contributing

## Project Overview

This service implements core Splitwise functionality:
- User signup/login (username/password + Google OAuth)
- JWT authentication for protected endpoints
- Group creation and membership
- Expense creation and splitting
- Settlement calculation and recording

The application exposes a REST API under various paths such as `/public`, `/user`, `/group`, `/expense`, and `/settle`.

## Tech Stack

- Java 17
- Spring Boot 3.3.x
- Spring Data JPA
- Spring Security
- PostgreSQL (runtime dependency)
- JWT (io.jsonwebtoken)
- Maven wrapper included

## Prerequisites

- JDK 17
- Maven (optional if you use the included Maven wrapper)
- PostgreSQL (or change datasource to an embedded DB for quick tests)
- (Optional) Google OAuth credentials if you plan to use OAuth login

## Configuration

All runtime configuration lives in `src/main/resources/application.properties` and supports overriding via environment variables.

Key properties (defaults provided in the repo):

- `server.port` — default 9090 (can be overridden with `PORT` env var).
- `spring.datasource.url` — JDBC URL for PostgreSQL. Default: `jdbc:postgresql://localhost:5432/splitwise`.
- `spring.datasource.username` — DB username (default `postgres`).
- `spring.datasource.password` — DB password (default `root`).
- `spring.jpa.hibernate.ddl-auto` — default `update` (adjust for production).
- Google OAuth:
  - `spring.security.oauth2.client.registration.google.client-id`
  - `spring.security.oauth2.client.registration.google.client-secret`
  - `spring.security.oauth2.client.registration.google.redirect-uri`


## Build & Run (Windows PowerShell)

From the project root (`D:\Sources\Splitwise\backend`) you can use the included Maven wrapper.

Build the project and create the executable JAR:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Run the packaged JAR:

```powershell
java -jar target\splitwise.jar
```

Or run directly with the Maven wrapper (useful during development):

```powershell
.\\mvnw.cmd spring-boot:run
```

If you prefer `mvn` directly (Maven must be installed):

```powershell
mvn clean package
java -jar target\splitwise.jar
```

## Environment variables (examples)

On Windows PowerShell you can set them inline before running, or set them in your environment.

Example (PowerShell):

```powershell
$env:PORT = "9090"
$env:GOOGLE_CLIENT_ID = "your-google-client-id"
$env:GOOGLE_CLIENT_SECRET = "your-google-client-secret"
$env:GOOGLE_REDIRECT_URI = "http://localhost:9090/public/oauth/callback"
# then run .\mvnw.cmd spring-boot:run
```

## API - common endpoints

Authentication
- POST /public/signup
  - Body: User JSON (userName, email, password, ...)
  - Returns: created user DTO

- POST /public/login
  - Body: { "userName": "...", "password": "..." }
  - Returns: JWT token (string)

- GET /public/healthCheck
  - Returns basic health message

OAuth (Google)
- The app supports Google OAuth flow. Redirect users to Google's consent screen and then POST the `code` to:
  - POST /public/oauth/callback?code=<authorization_code>
  - Returns: { "token": "<jwt>" }

Using the JWT
- For protected endpoints include the token in the `Authorization` header:

  Authorization: Bearer <jwt>

User endpoints (authenticated)
- GET /user — current user details
- GET /user/getBalance/all — fetch balances across all groups
- POST /user/{userName}/role/{roleName} — add a role to a user (admin privilege may be required depending on security setup)

Group endpoints (authenticated)
- POST /group — create group (body: Group JSON)
- POST /group/{groupName}/user/{userName} — add user to group
- GET /group/{groupName} — fetch group details
- GET /group/getBalance/{groupName} — fetch balances for a group

Expense endpoints (authenticated)
- POST /expense — create expense (request DTO: `CreateExpenseDto`)

Settlement endpoints (authenticated)
- GET /settle/group/{groupName} — compute minimal settlement transactions for the group
- POST /settle/group/{groupName} — record an actual settle-up transaction (body: `SettleUpDTO`)
- GET /settle/getAllSettled/user — consolidated settled transactions for logged user

Notes
- Exact request/response shapes are defined in `src/main/java/com/project/splitwise/dto/`.
- Check controller classes under `src/main/java/com/project/splitwise/controller` for endpoint behavior and validation messages.

## Database

By default the app connects to a PostgreSQL database at `jdbc:postgresql://localhost:5432/splitwise`.

Create the database and user (example):

```sql
-- run in psql or any PostgreSQL client
CREATE DATABASE splitwise;
-- create or use existing user and grant privileges
-- ALTER ROLE postgres WITH PASSWORD 'root';
-- GRANT ALL PRIVILEGES ON DATABASE splitwise TO postgres;
```

The project uses JPA `hibernate.ddl-auto=update` in development mode to adjust schema automatically; for production, prefer explicit migrations.

## Tests

Run unit tests with:

```powershell
.\mvnw.cmd test
```

## Troubleshooting

- Port already in use: change `server.port` or `PORT` env var.
- Database connection failures: ensure PostgreSQL is running and credentials/URL are correct.
- OAuth problems: make sure the redirect URI matches the one configured in Google Cloud Console.
- Missing/invalid JWT: check the `Authorization` header format and token expiry.

## Contributing

- Follow standard Git workflow (feature branch, PR, review).
- Keep secrets out of commits.
- Add tests for new behavior where appropriate.

## Useful files

- `pom.xml` — Maven configuration and dependencies
- `src/main/resources/application.properties` — runtime configuration
- `src/main/java/com/project/splitwise/controller` — REST controllers and endpoints
- `src/main/java/com/project/splitwise/dto` — data transfer objects

---

If you want, I can also:
- Add an OpenAPI/Swagger spec and UI for easier API exploration.
- Provide Postman or HTTPie collection with example requests.
- Add Dockerfile and docker-compose for local DB + app orchestration.


