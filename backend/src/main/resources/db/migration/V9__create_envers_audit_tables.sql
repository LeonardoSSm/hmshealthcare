CREATE TABLE IF NOT EXISTS revinfo (
  rev INT PRIMARY KEY AUTO_INCREMENT,
  revtstmp BIGINT
);

CREATE TABLE IF NOT EXISTS medical_records_aud (
  rev INT NOT NULL,
  revtype TINYINT,
  id VARCHAR(36) NOT NULL,
  patient_id VARCHAR(36),
  observations TEXT NULL,
  created_at TIMESTAMP NULL,
  updated_at TIMESTAMP NULL,
  PRIMARY KEY (id, rev),
  CONSTRAINT fk_medical_records_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE INDEX idx_medical_records_aud_rev ON medical_records_aud(rev);

CREATE TABLE IF NOT EXISTS diagnoses_aud (
  rev INT NOT NULL,
  revtype TINYINT,
  id BIGINT NOT NULL,
  medical_record_id VARCHAR(36),
  doctor_id VARCHAR(36),
  icd10_code VARCHAR(16),
  description VARCHAR(255),
  notes TEXT NULL,
  date TIMESTAMP NULL,
  PRIMARY KEY (id, rev),
  CONSTRAINT fk_diagnoses_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE INDEX idx_diagnoses_aud_rev ON diagnoses_aud(rev);

CREATE TABLE IF NOT EXISTS prescriptions_aud (
  rev INT NOT NULL,
  revtype TINYINT,
  id BIGINT NOT NULL,
  medical_record_id VARCHAR(36),
  doctor_id VARCHAR(36),
  medication VARCHAR(255),
  dosage VARCHAR(120),
  frequency VARCHAR(120),
  start_date TIMESTAMP NULL,
  end_date TIMESTAMP NULL,
  PRIMARY KEY (id, rev),
  CONSTRAINT fk_prescriptions_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE INDEX idx_prescriptions_aud_rev ON prescriptions_aud(rev);
