INSERT INTO users (id, name, email, password_hash, role, active, created_at)
VALUES (
  '11111111-1111-1111-1111-111111111111',
  'Dra. Ana Ferreira',
  'dr.ana@medicore.local',
  '$2a$10$CPSDp1Q/AfDpkjaCufY8yuEan7YPlwIqZrz6bInO38PHhumvkX.xK',
  'DOCTOR',
  TRUE,
  CURRENT_TIMESTAMP
)
ON DUPLICATE KEY UPDATE email = email;
