CREATE TABLE IF NOT EXISTS attendances (
  id VARCHAR(36) PRIMARY KEY,
  ticket_number VARCHAR(20) NOT NULL UNIQUE,
  patient_id VARCHAR(36) NOT NULL,
  status VARCHAR(30) NOT NULL,
  risk_level VARCHAR(20) NULL,
  priority_score INT NOT NULL,
  nurse_id VARCHAR(36) NULL,
  doctor_id VARCHAR(36) NULL,
  room_label VARCHAR(40) NULL,
  check_in_notes VARCHAR(500) NULL,
  triage_notes TEXT NULL,
  consultation_notes TEXT NULL,
  outcome TEXT NULL,
  check_in_at TIMESTAMP NOT NULL,
  triage_started_at TIMESTAMP NULL,
  triage_finished_at TIMESTAMP NULL,
  called_at TIMESTAMP NULL,
  consultation_started_at TIMESTAMP NULL,
  consultation_finished_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  version BIGINT NOT NULL DEFAULT 0,
  open_patient_id VARCHAR(36)
    GENERATED ALWAYS AS (
      CASE
        WHEN status IN ('WAITING_TRIAGE', 'IN_TRIAGE', 'WAITING_DOCTOR', 'CALLED_DOCTOR', 'IN_CONSULTATION')
          THEN patient_id
        ELSE NULL
      END
    ) STORED,
  CONSTRAINT fk_attendances_patient FOREIGN KEY (patient_id) REFERENCES patients(id),
  CONSTRAINT fk_attendances_nurse FOREIGN KEY (nurse_id) REFERENCES users(id),
  CONSTRAINT fk_attendances_doctor FOREIGN KEY (doctor_id) REFERENCES users(id)
);

CREATE UNIQUE INDEX uq_attendances_open_patient ON attendances(open_patient_id);
CREATE INDEX idx_attendances_status_priority ON attendances(status, priority_score, check_in_at);
CREATE INDEX idx_attendances_checkin ON attendances(check_in_at);

CREATE TABLE IF NOT EXISTS attendance_status_history (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  attendance_id VARCHAR(36) NOT NULL,
  from_status VARCHAR(30) NULL,
  to_status VARCHAR(30) NOT NULL,
  changed_by VARCHAR(120) NOT NULL,
  note VARCHAR(500) NULL,
  changed_at TIMESTAMP NOT NULL,
  CONSTRAINT fk_attendance_status_history_attendance FOREIGN KEY (attendance_id) REFERENCES attendances(id)
);

CREATE INDEX idx_attendance_status_history_attendance ON attendance_status_history(attendance_id);
CREATE INDEX idx_attendance_status_history_changed_at ON attendance_status_history(changed_at);
