UPDATE users
SET password_hash = '$2a$10$CPSDp1Q/AfDpkjaCufY8yuEan7YPlwIqZrz6bInO38PHhumvkX.xK'
WHERE id = '00000000-0000-0000-0000-000000000001'
  AND email = 'admin@medicore.local';
