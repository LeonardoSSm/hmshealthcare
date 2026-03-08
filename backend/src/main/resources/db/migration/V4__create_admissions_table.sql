CREATE TABLE IF NOT EXISTS admissions (
  id VARCHAR(36) PRIMARY KEY,
  patient_id VARCHAR(36) NOT NULL,
  bed_id VARCHAR(36) NOT NULL,
  doctor_id VARCHAR(36) NOT NULL,
  admission_date TIMESTAMP NOT NULL,
  discharge_date TIMESTAMP NULL,
  reason VARCHAR(255) NOT NULL,
  status VARCHAR(20) NOT NULL,
  CONSTRAINT fk_admissions_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
  CONSTRAINT fk_admissions_bed FOREIGN KEY (bed_id) REFERENCES beds(id)
);

CREATE INDEX idx_admissions_patient ON admissions(patient_id);
CREATE INDEX idx_admissions_status ON admissions(status);
