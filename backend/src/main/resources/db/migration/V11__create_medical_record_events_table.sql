CREATE TABLE IF NOT EXISTS medical_record_events (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  medical_record_id VARCHAR(36) NOT NULL,
  type VARCHAR(32) NOT NULL,
  author VARCHAR(120) NOT NULL,
  description VARCHAR(1000) NOT NULL,
  notes TEXT NULL,
  occurred_at TIMESTAMP NOT NULL,
  CONSTRAINT fk_medical_record_events_record FOREIGN KEY (medical_record_id) REFERENCES medical_records(id)
);

CREATE INDEX idx_medical_record_events_record ON medical_record_events(medical_record_id);
CREATE INDEX idx_medical_record_events_occurred_at ON medical_record_events(occurred_at);

CREATE TABLE IF NOT EXISTS medical_record_events_aud (
  rev INT NOT NULL,
  revtype TINYINT,
  id BIGINT NOT NULL,
  medical_record_id VARCHAR(36),
  type VARCHAR(32),
  author VARCHAR(120),
  description VARCHAR(1000),
  notes TEXT NULL,
  occurred_at TIMESTAMP NULL,
  PRIMARY KEY (id, rev),
  CONSTRAINT fk_medical_record_events_aud_rev FOREIGN KEY (rev) REFERENCES revinfo(rev)
);

CREATE INDEX idx_medical_record_events_aud_rev ON medical_record_events_aud(rev);
