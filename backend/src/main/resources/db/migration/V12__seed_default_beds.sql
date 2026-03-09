INSERT INTO beds (id, number, floor, ward, type, status)
VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1', 'UTI-01', 2, 'UTI', 'UTI', 'AVAILABLE'),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2', 'UTI-02', 2, 'UTI', 'UTI', 'AVAILABLE'),
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3', 'UTI-03', 2, 'UTI', 'UTI', 'AVAILABLE'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1', 'ENF-01', 3, 'Enfermaria A', 'WARD', 'AVAILABLE'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2', 'ENF-02', 3, 'Enfermaria A', 'WARD', 'AVAILABLE'),
  ('cccccccc-cccc-cccc-cccc-ccccccccccc1', 'QTO-01', 4, 'Quartos', 'PRIVATE', 'AVAILABLE'),
  ('cccccccc-cccc-cccc-cccc-ccccccccccc2', 'QTO-02', 4, 'Quartos', 'PRIVATE', 'AVAILABLE')
ON DUPLICATE KEY UPDATE id = id;
