# Library Study Room Reservation System

A Spring Boot REST API for reserving library study rooms and seats. Built with Kotlin, Spring Security (JWT), and PostgreSQL.

## Documentation

- [Roles & Permissions](docs/roles-permissions.md) — roles, permission catalogue, JWT claims model
- [Domain Models](docs/models.md) — entity definitions and relationships
- [API Reference](docs/api.md) — all endpoints with access levels
- [Configuration](docs/configuration.md) — application properties and environment variables

## Tech Stack

- **Language:** Kotlin
- **Framework:** Spring Boot 4
- **Security:** Spring Security + JWT (JJWT)
- **Database:** PostgreSQL 17
- **Build:** Gradle (Kotlin DSL)
- **Java:** 24

## Running Locally

**With Docker Compose (recommended):**
```bash
docker compose up --build
```
App will be available at `http://localhost:8080`. PostgreSQL runs on `localhost:5432`.

**Without Docker (app only):**
```bash
# Requires a running PostgreSQL instance on localhost:5432
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Project Structure

```
src/main/kotlin/md/usm/teza/reservare/
├── auth/           # Login, registration, JWT issuance
├── user/           # User management
├── room/           # Study room management
├── reservation/    # Reservation lifecycle + booking rules
├── config/         # Security config, JWT filter, app properties
└── common/         # Exception handling, response envelope
```
