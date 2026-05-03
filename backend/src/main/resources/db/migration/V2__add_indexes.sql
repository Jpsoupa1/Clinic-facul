-- ============================================================
-- V2 — Índices para otimização de consultas frequentes
-- ============================================================

CREATE INDEX idx_users_email          ON users (email);
CREATE INDEX idx_users_role           ON users (role);

CREATE INDEX idx_doctors_crm          ON doctors (crm);
CREATE INDEX idx_doctors_specialty    ON doctors (specialty);
CREATE INDEX idx_doctors_active       ON doctors (active);

CREATE INDEX idx_patients_cpf         ON patients (cpf);
CREATE INDEX idx_patients_name        ON patients (name);
CREATE INDEX idx_patients_active      ON patients (active);

CREATE INDEX idx_appointments_doctor  ON appointments (doctor_id);
CREATE INDEX idx_appointments_patient ON appointments (patient_id);
CREATE INDEX idx_appointments_status  ON appointments (status);
CREATE INDEX idx_appointments_dt      ON appointments (date_time);
CREATE INDEX idx_appointments_doctor_dt ON appointments (doctor_id, date_time);

CREATE INDEX idx_medical_patient      ON medical_records (patient_id);
CREATE INDEX idx_medical_doctor       ON medical_records (doctor_id);
CREATE INDEX idx_medical_created      ON medical_records (created_at);
