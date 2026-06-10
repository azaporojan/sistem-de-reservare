-- ============================================================
-- V2: Individual seats per room + seat-level reservations
-- ============================================================

CREATE TABLE seats (
    id      BIGSERIAL   PRIMARY KEY,
    room_id BIGINT      NOT NULL REFERENCES study_rooms(id) ON DELETE CASCADE,
    label   VARCHAR(20) NOT NULL,
    row_no  INT         NOT NULL,
    col_no  INT         NOT NULL,
    UNIQUE (room_id, label)
);

CREATE INDEX idx_seats_room_id ON seats(room_id);

-- Backfill: auto-generate seats for existing rooms in a grid
-- (cols = ceil(sqrt(capacity)), labels S1..Sn)
INSERT INTO seats (room_id, label, row_no, col_no)
SELECT r.id,
       'S' || n,
       ((n - 1) / CEIL(SQRT(r.capacity))::int) + 1,
       ((n - 1) % CEIL(SQRT(r.capacity))::int) + 1
FROM study_rooms r
CROSS JOIN LATERAL generate_series(1, r.capacity) AS n;

-- Seat reservations now reference a concrete seat
ALTER TABLE reservations ADD COLUMN seat_id BIGINT REFERENCES seats(id);
CREATE INDEX idx_reservations_seat_id ON reservations(seat_id);
