# Library Study Room Reservation System

A Spring Boot REST API for reserving library study rooms and seats. Built with Kotlin, Spring Security (JWT), and PostgreSQL.

## Documentation

- [Roles & Permissions](docs/roles-permissions.md) — roles, permission catalogue, booking rules, JWT claims
- [Domain Models](docs/models.md) — entity definitions, field types, and relationships
- [API Reference](docs/api.md) — all endpoints with access levels and examples
- [Configuration](docs/configuration.md) — application properties and environment variables
- [Test Users](docs/test-users.md) — seeded MVP accounts and a suggested test flow
- [UI Requirements](docs/UI_REQUIREMENTS.md) — frontend architecture, flows, and progress

## Tech Stack

- **Language:** Kotlin
- **Framework:** Spring Boot 4.0.6
- **Security:** Spring Security + JWT (JJWT 0.12.6)
- **Database:** PostgreSQL 17
- **Migrations:** Flyway
- **API Docs:** SpringDoc OpenAPI (Swagger UI at `/swagger-ui.html`)
- **Build:** Gradle (Kotlin DSL)
- **Java:** 24

## Running Locally

**With Docker Compose (recommended):**
```bash
docker compose up --build
```
App: `http://localhost:8080` — PostgreSQL: `localhost:5432`

**Without Docker:**
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```
Requires a running PostgreSQL instance on `localhost:5432`.

## Project Structure

```
src/main/kotlin/md/usm/teza/reservare/
├── auth/           # Register, login, token refresh (AuthController, AuthService, RefreshToken)
├── user/           # User CRUD, role & permission management (UserController, UserService, entities)
├── room/           # Study room CRUD and availability (RoomController, RoomService, StudyRoom)
├── reservation/    # Booking, review, cancellation, completion scheduler
│   ├── ReservationController / ReservationService
│   ├── BookingRulesValidator   # Slot validation, role-based booking rules
│   └── CompletionScheduler     # @Scheduled — marks expired reservations COMPLETED
├── security/       # JwtService, JwtAuthFilter
├── config/         # SecurityConfig, AppProperties, FlywayConfig, OpenApiConfig
└── common/         # GlobalExceptionHandler, ErrorResponse, custom exceptions
```

## Seeded Accounts

All test accounts (MVP only) are documented in [docs/test-users.md](docs/test-users.md):

| Email | Password | Role |
|---|---|---|
| `admin@library.local` | `Admin1234!` | ADMIN (V1) |
| `admin2@library.local` | `Admin1234!` | ADMIN |
| `teacher@library.local` | `Teacher1234!` | TEACHER |
| `student1@library.local` | `Student1234!` | STUDENT |
| `student2@library.local` | `Student1234!` | STUDENT |
