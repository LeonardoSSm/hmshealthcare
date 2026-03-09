-- Disable seeded local users in hardened environments (controlled by Flyway placeholder).
UPDATE users
SET active = CASE
  WHEN ${disable_seeded_users} THEN FALSE
  ELSE active
END
WHERE email IN ('admin@medicore.local', 'dr.ana@medicore.local');

-- Enforce doctor referential integrity.
ALTER TABLE admissions
  ADD CONSTRAINT fk_admissions_doctor FOREIGN KEY (doctor_id) REFERENCES users(id);

ALTER TABLE diagnoses
  ADD CONSTRAINT fk_diagnoses_doctor FOREIGN KEY (doctor_id) REFERENCES users(id);

ALTER TABLE prescriptions
  ADD CONSTRAINT fk_prescriptions_doctor FOREIGN KEY (doctor_id) REFERENCES users(id);

-- Enforce at most one active admission per patient and per bed.
ALTER TABLE admissions
  ADD COLUMN active_patient_id VARCHAR(36)
    GENERATED ALWAYS AS (CASE WHEN status = 'ACTIVE' THEN patient_id ELSE NULL END) STORED,
  ADD COLUMN active_bed_id VARCHAR(36)
    GENERATED ALWAYS AS (CASE WHEN status = 'ACTIVE' THEN bed_id ELSE NULL END) STORED;

CREATE UNIQUE INDEX uq_admissions_active_patient ON admissions(active_patient_id);
CREATE UNIQUE INDEX uq_admissions_active_bed ON admissions(active_bed_id);
CREATE INDEX idx_admissions_bed_status ON admissions(bed_id, status);

-- Refresh token lookup path hardening.
CREATE UNIQUE INDEX uq_refresh_tokens_hash ON refresh_tokens(token_hash);
