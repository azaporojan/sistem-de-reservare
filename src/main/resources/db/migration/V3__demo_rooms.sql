-- ============================================================
-- V3: Demo rooms with auto-generated seats
-- ============================================================

INSERT INTO study_rooms (name, location, capacity, description, active) VALUES
    ('Reading Room A', 'Building 1, Floor 2', 30, 'Large reading room with natural light', TRUE),
    ('Study Hall B',   'Building 1, Floor 3', 24, 'Group study hall',                     TRUE),
    ('Quiet Room C',   'Building 2, Floor 1', 12, 'Silent individual study room',         TRUE);

-- Seats: same grid formula as V2 (cols = ceil(sqrt(capacity)), labels S1..Sn)
INSERT INTO seats (room_id, label, row_no, col_no)
SELECT r.id,
       'S' || n,
       ((n - 1) / CEIL(SQRT(r.capacity))::int) + 1,
       ((n - 1) % CEIL(SQRT(r.capacity))::int) + 1
FROM study_rooms r
CROSS JOIN LATERAL generate_series(1, r.capacity) AS n
WHERE r.name IN ('Reading Room A', 'Study Hall B', 'Quiet Room C')
  AND NOT EXISTS (SELECT 1 FROM seats s WHERE s.room_id = r.id);
