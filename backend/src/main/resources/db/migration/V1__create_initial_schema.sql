-- ============================================================
-- V1 — Schema inicial do CRM Médico
-- AXIS Lab | 2026
-- ============================================================

-- Tabela de usuários do sistema (médicos, recepcionistas, admins)
CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL,
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP
);

-- Tabela de médicos (referencia users)
CREATE TABLE doctors (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT       NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    crm        VARCHAR(20)  NOT NULL UNIQUE,
    specialty  VARCHAR(100) NOT NULL,
    phone      VARCHAR(20),
    active     BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Tabela de pacientes (sem conta no sistema)
CREATE TABLE patients (
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(150) NOT NULL,
    cpf                VARCHAR(14)  NOT NULL UNIQUE,
    email              VARCHAR(255),
    birth_date         DATE         NOT NULL,
    phone              VARCHAR(20),
    emergency_contact  VARCHAR(200),
    blood_type         VARCHAR(10),
    allergies          TEXT,
    insurance_provider VARCHAR(100),
    insurance_number   VARCHAR(50),
    active             BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP    NOT NULL,
    updated_at         TIMESTAMP
);

-- Tabela de consultas / agendamentos
CREATE TABLE appointments (
    id                  BIGSERIAL PRIMARY KEY,
    doctor_id           BIGINT    NOT NULL REFERENCES doctors (id),
    patient_id          BIGINT    NOT NULL REFERENCES patients (id),
    date_time           TIMESTAMP NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    payment_type        VARCHAR(20) NOT NULL,
    notes               TEXT,
    cancellation_reason TEXT,
    created_at          TIMESTAMP NOT NULL,
    updated_at          TIMESTAMP
);

-- Tabela de prontuários eletrônicos
CREATE TABLE medical_records (
    id             BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT    NOT NULL UNIQUE REFERENCES appointments (id),
    doctor_id      BIGINT    NOT NULL REFERENCES doctors (id),
    patient_id     BIGINT    NOT NULL REFERENCES patients (id),
    anamnesis      TEXT      NOT NULL,
    diagnosis      TEXT      NOT NULL,
    cid_code       VARCHAR(10),
    prescription   TEXT,
    evolution      TEXT,
    created_at     TIMESTAMP NOT NULL
);
