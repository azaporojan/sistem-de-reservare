-- ============================================================
-- V1__init.sql  –  Library Study Room Reservation System
-- ============================================================

-- ── Users ────────────────────────────────────────────────────
CREATE TABLE users (
    id         UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(255) NOT NULL,
    enabled    BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ── Roles ────────────────────────────────────────────────────
CREATE TABLE roles (
    id   BIGSERIAL   PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- One role per user
CREATE TABLE user_roles (
    id      BIGSERIAL PRIMARY KEY,
    user_id UUID      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT    NOT NULL REFERENCES roles(id),
    UNIQUE (user_id)
);

-- ── Permissions ──────────────────────────────────────────────
CREATE TABLE permissions (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE role_permissions (
    id            BIGSERIAL PRIMARY KEY,
    role_id       BIGINT NOT NULL REFERENCES roles(id)       ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE (role_id, permission_id)
);

-- Extra permissions granted directly to a user
CREATE TABLE user_permissions (
    id            BIGSERIAL PRIMARY KEY,
    user_id       UUID   NOT NULL REFERENCES users(id)       ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    UNIQUE (user_id, permission_id)
);

-- ── Study Rooms ──────────────────────────────────────────────
CREATE TABLE study_rooms (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(255) NOT NULL UNIQUE,
    location    VARCHAR(255) NOT NULL,
    capacity    INT          NOT NULL CHECK (capacity > 0),
    description TEXT,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ── Reservations ─────────────────────────────────────────────
CREATE TABLE reservations (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id         UUID        NOT NULL REFERENCES users(id),
    room_id         BIGINT      NOT NULL REFERENCES study_rooms(id),
    type            VARCHAR(10) NOT NULL CHECK (type IN ('ROOM','SEAT')),
    start_date_time TIMESTAMP   NOT NULL,
    end_date_time   TIMESTAMP   NOT NULL,
    seats_requested INT         NOT NULL CHECK (seats_requested > 0),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                        CHECK (status IN ('PENDING','CONFIRMED','COMPLETED','DECLINED','CANCELLED')),
    notes           TEXT,
    reviewed_by     UUID        REFERENCES users(id),
    review_note     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_reservations_user_id  ON reservations(user_id);
CREATE INDEX idx_reservations_room_id  ON reservations(room_id);
CREATE INDEX idx_reservations_status   ON reservations(status);
CREATE INDEX idx_reservations_dates    ON reservations(start_date_time, end_date_time);

-- ── Refresh Tokens ───────────────────────────────────────────
CREATE TABLE refresh_tokens (
    id         BIGSERIAL   PRIMARY KEY,
    token      VARCHAR(512) NOT NULL UNIQUE,
    user_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMPTZ  NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- ============================================================
-- Seed data
-- ============================================================

-- Roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('TEACHER'), ('STUDENT');

-- Permissions
INSERT INTO permissions (name, description) VALUES
    ('ALL',                    'Full access to all resources'),
    ('ROOM_READ',              'View study rooms'),
    ('ROOM_WRITE',             'Create/update/delete study rooms'),
    ('ROOM_BOOK',              'Reserve an entire room'),
    ('SEAT_BOOK',              'Reserve a seat in a room'),
    ('RESERVATION_READ_OWN',   'View own reservations'),
    ('RESERVATION_READ_ALL',   'View all reservations'),
    ('RESERVATION_CANCEL_OWN', 'Cancel own reservations'),
    ('RESERVATION_MANAGE',     'Confirm/decline/cancel any reservation'),
    ('USER_READ',              'View user accounts'),
    ('USER_MANAGE',            'Create/update/delete user accounts');

-- Role → Permission mappings
-- ADMIN: ALL
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name = 'ALL';

-- TEACHER: ROOM_READ, ROOM_BOOK, RESERVATION_READ_OWN, RESERVATION_CANCEL_OWN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'TEACHER'
  AND p.name IN ('ROOM_READ','ROOM_BOOK','RESERVATION_READ_OWN','RESERVATION_CANCEL_OWN');

-- STUDENT: ROOM_READ, SEAT_BOOK, RESERVATION_READ_OWN, RESERVATION_CANCEL_OWN
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'STUDENT'
  AND p.name IN ('ROOM_READ','SEAT_BOOK','RESERVATION_READ_OWN','RESERVATION_CANCEL_OWN');

-- Default admin user  (password: Admin1234!)
-- bcrypt hash generated with cost 10
INSERT INTO users (id, email, password, full_name, enabled)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin@library.local',
    '$2a$10$cdqzuSd5qSEEl.onSm7Zr.T.GcfQJ7sM26D3634jYisKlWMb52oLS',
    'System Administrator',
    TRUE
);

INSERT INTO user_roles (user_id, role_id)
SELECT '00000000-0000-0000-0000-000000000001', id FROM roles WHERE name = 'ADMIN';
