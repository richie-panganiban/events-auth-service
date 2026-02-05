-- Seed admin user
INSERT INTO admins (id, email, name, is_active, created_at)
VALUES (gen_random_uuid(), 'richie@chicodetech.com', 'Richie', true, NOW());
