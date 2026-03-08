CREATE TABLE IF NOT EXISTS diagnoses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  medical_record_id VARCHAR(36) NOT NULL,
  doctor_id VARCHAR(36) NOT NULL,
  icd10_code VARCHAR(16) NOT NULL,
  description VARCHAR(255) NOT NULL,
  notes TEXT NULL,
  date TIMESTAMP NOT NULL,
  CONSTRAINT fk_diagnoses_record FOREIGN KEY (medical_record_id) REFERENCES medical_records(id)
);

CREATE TABLE IF NOT EXISTS prescriptions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  medical_record_id VARCHAR(36) NOT NULL,
  doctor_id VARCHAR(36) NOT NULL,
  medication VARCHAR(255) NOT NULL,
  dosage VARCHAR(120) NOT NULL,
  frequency VARCHAR(120) NOT NULL,
  start_date TIMESTAMP NOT NULL,
  end_date TIMESTAMP NOT NULL,
  CONSTRAINT fk_prescriptions_record FOREIGN KEY (medical_record_id) REFERENCES medical_records(id)
);
