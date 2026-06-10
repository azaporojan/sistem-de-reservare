-- ============================================================
-- V4: Test users (MVP only — remove before real production)
--
--   student1@library.local / Student1234!   (STUDENT)
--   student2@library.local / Student1234!   (STUDENT)
--   teacher@library.local  / Teacher1234!   (TEACHER)
--   admin2@library.local   / Admin1234!     (ADMIN)
-- (plus the original admin@library.local / Admin1234! from V1)
-- ============================================================

INSERT INTO users (id, email, password, full_name, enabled) VALUES
    ('00000000-0000-0000-0000-000000000101', 'student1@library.local',
     '$2a$10$YflDVtEzGl21IC.OPb4bDuKWQ8lgiftSXM4ynG9fy5.IFi7TGXfuK', 'Ana Popescu',     TRUE),
    ('00000000-0000-0000-0000-000000000102', 'student2@library.local',
     '$2a$10$YflDVtEzGl21IC.OPb4bDuKWQ8lgiftSXM4ynG9fy5.IFi7TGXfuK', 'Ion Rusu',        TRUE),
    ('00000000-0000-0000-0000-000000000103', 'teacher@library.local',
     '$2a$10$mN6a9NlMndEqPsKTvcW3kOXZ9P8rDUl3D9edVG0nDeHN0CL55tEju', 'Prof. Maria Cojocaru', TRUE),
    ('00000000-0000-0000-0000-000000000104', 'admin2@library.local',
     '$2a$10$cdqzuSd5qSEEl.onSm7Zr.T.GcfQJ7sM26D3634jYisKlWMb52oLS', 'Second Admin',    TRUE);

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE (u.email IN ('student1@library.local', 'student2@library.local') AND r.name = 'STUDENT')
   OR (u.email = 'teacher@library.local' AND r.name = 'TEACHER')
   OR (u.email = 'admin2@library.local'  AND r.name = 'ADMIN');
