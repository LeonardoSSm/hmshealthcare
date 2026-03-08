INSERT INTO users (id, name, email, password_hash, role, active, created_at)
VALUES (
  '00000000-0000-0000-0000-000000000001',
  'Admin User',
  'admin@medicore.local',
  '$2a$12$uNf9z34x0v2kM8Q6bY4P1eZVhM.gWv6m4fzV6bT2F0IYkUjNtu4SO',
  'ADMIN',
  TRUE,
  CURRENT_TIMESTAMP
)
ON DUPLICATE KEY UPDATE email = email;
