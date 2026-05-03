# CRM Médico e Clínico — Documentação Técnica do Backend

**Projeto:** CRM Médico | **Equipe:** João Sousa Passos, Yuri Macedo Bolis, Vinicius Coelho, Victor Hugo  
**Versão:** 1.0.0 | **Stack:** Java 21 · Spring Boot 3.3 · PostgreSQL 16 · Docker · Flyway · JWT

---

## 1. Visão Geral da Arquitetura

O backend segue o padrão **Layered Architecture (Monolito Modular)**:

```
Controller  →  Service  →  Repository  →  Banco de Dados
    ↓              ↓
  DTO (in)     Entidade JPA
    ↓              ↓
  DTO (out)   Flyway Migration
```

### Tecnologias Utilizadas

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 21 LTS |
| Framework | Spring Boot 3.3.6 |
| ORM | Spring Data JPA / Hibernate |
| Banco | PostgreSQL 16 |
| Migrações | Flyway |
| Segurança | Spring Security 6 + JWT (JJWT 0.12.3) |
| Documentação | SpringDoc OpenAPI 2.6 (Swagger UI) |
| Containers | Docker Compose |

---

## 2. Estrutura de Diretórios

```
backend/
├── src/main/java/com/axislab/crmmedico/
│   ├── CrmMedicoApplication.java       # Ponto de entrada
│   ├── config/
│   │   ├── DataInitializer.java        # Cria admin na 1ª execução
│   │   └── OpenApiConfig.java          # Configuração Swagger
│   ├── controller/
│   │   ├── AuthController.java         # POST /auth/login, /auth/register
│   │   ├── DoctorController.java       # CRUD /doctors
│   │   ├── PatientController.java      # CRUD /patients
│   │   ├── AppointmentController.java  # CRUD /appointments
│   │   └── MedicalRecordController.java# CRUD /medical-records
│   ├── dto/
│   │   ├── request/                    # Java Records de entrada (validados)
│   │   └── response/                   # Java Records de saída (com factory from())
│   ├── entity/                         # Entidades JPA mapeadas ao banco
│   ├── enums/                          # RoleType, AppointmentStatus, etc.
│   ├── exception/                      # Exceções e GlobalExceptionHandler
│   ├── repository/                     # Interfaces JpaRepository + queries customizadas
│   ├── security/                       # JWT, JwtFilter, SecurityConfig
│   └── service/                        # Toda a lógica de negócio
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       ├── V1__create_initial_schema.sql
│       └── V2__add_indexes.sql
└── pom.xml
```

---

## 3. Modelo de Dados

### 3.1 Entidades e Relacionamentos

```
users (1) ──── (1) doctors ──── (N) appointments ──── (1) medical_records
                                        │
patients (1) ──────────────────────────(N)
```

### 3.2 Tabelas

#### `users`
| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | BIGSERIAL | PK |
| name | VARCHAR(150) | NOT NULL |
| email | VARCHAR(255) | NOT NULL, UNIQUE |
| password | VARCHAR(255) | NOT NULL (BCrypt) |
| role | VARCHAR(20) | NOT NULL (`ADMIN`, `MEDICO`, `RECEPCIONISTA`) |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at | TIMESTAMP | NOT NULL |
| updated_at | TIMESTAMP | |

#### `doctors`
| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | BIGSERIAL | PK |
| user_id | BIGINT | FK → users(id), UNIQUE |
| crm | VARCHAR(20) | NOT NULL, UNIQUE |
| specialty | VARCHAR(100) | NOT NULL |
| phone | VARCHAR(20) | |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE |

#### `patients`
| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | BIGSERIAL | PK |
| name | VARCHAR(150) | NOT NULL |
| cpf | VARCHAR(14) | NOT NULL, UNIQUE |
| email | VARCHAR(255) | (contato, não é login) |
| birth_date | DATE | NOT NULL |
| phone | VARCHAR(20) | |
| emergency_contact | VARCHAR(200) | |
| blood_type | VARCHAR(10) | (`A_POS`, `A_NEG`, ...) |
| allergies | TEXT | |
| insurance_provider | VARCHAR(100) | |
| insurance_number | VARCHAR(50) | |
| active | BOOLEAN | NOT NULL, DEFAULT TRUE |
| created_at / updated_at | TIMESTAMP | |

#### `appointments`
| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | BIGSERIAL | PK |
| doctor_id | BIGINT | FK → doctors(id) |
| patient_id | BIGINT | FK → patients(id) |
| date_time | TIMESTAMP | NOT NULL |
| status | VARCHAR(20) | `AGENDADA`, `CONFIRMADA`, `REALIZADA`, `CANCELADA`, `NAO_COMPARECEU` |
| payment_type | VARCHAR(20) | `PARTICULAR`, `CONVENIO`, `SUS` |
| notes | TEXT | |
| cancellation_reason | TEXT | |
| created_at / updated_at | TIMESTAMP | |

#### `medical_records`
| Coluna | Tipo | Restrição |
|--------|------|-----------|
| id | BIGSERIAL | PK |
| appointment_id | BIGINT | FK → appointments(id), UNIQUE |
| doctor_id | BIGINT | FK → doctors(id) |
| patient_id | BIGINT | FK → patients(id) |
| anamnesis | TEXT | NOT NULL |
| diagnosis | TEXT | NOT NULL |
| cid_code | VARCHAR(10) | |
| prescription | TEXT | |
| evolution | TEXT | |
| created_at | TIMESTAMP | NOT NULL |

---

## 4. Enums do Sistema

```java
// Perfis de acesso
RoleType: ADMIN | MEDICO | RECEPCIONISTA

// Status da consulta (fluxo)
AppointmentStatus: AGENDADA → CONFIRMADA → REALIZADA
                           └──→ CANCELADA
                           └──→ NAO_COMPARECEU

// Tipo de pagamento
PaymentType: PARTICULAR | CONVENIO | SUS

// Tipo sanguíneo
BloodType: A_POS | A_NEG | B_POS | B_NEG | AB_POS | AB_NEG | O_POS | O_NEG
```

---

## 5. API REST — Endpoints

Base URL: `http://localhost:8080/api/v1`

### 5.1 Autenticação

| Método | Endpoint | Autenticação | Descrição |
|--------|----------|--------------|-----------|
| POST | `/auth/login` | Pública | Login — retorna JWT |
| POST | `/auth/register` | ADMIN | Registrar novo usuário do sistema |

#### POST `/auth/login`
```json
// Request
{ "email": "admin@axislab.com", "password": "Admin@123" }

// Response 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@axislab.com",
  "name": "Administrador",
  "role": "ADMIN"
}
```

#### POST `/auth/register` (ADMIN)
```json
// Request
{ "name": "Dr. Carlos", "email": "carlos@clinica.com", "password": "Senha@123", "role": "MEDICO" }
```

---

### 5.2 Médicos

| Método | Endpoint | Autenticação | Descrição |
|--------|----------|--------------|-----------|
| POST | `/doctors` | ADMIN | Cadastrar médico (cria conta de usuário) |
| GET | `/doctors` | Autenticado | Listar ativos (filtro: `?specialty=Cardio`) |
| GET | `/doctors/{id}` | Autenticado | Buscar por ID |
| PUT | `/doctors/{id}` | ADMIN | Atualizar dados |
| DELETE | `/doctors/{id}` | ADMIN | Desativar (soft delete) |

#### POST `/doctors`
```json
// Request
{
  "name": "Dr. Carlos Mendes",
  "email": "carlos@clinica.com",
  "password": "Senha@123",
  "crm": "SP-123456",
  "specialty": "Cardiologia",
  "phone": "(11) 99999-0001"
}

// Response 201 Created
{
  "id": 1,
  "name": "Dr. Carlos Mendes",
  "email": "carlos@clinica.com",
  "crm": "SP-123456",
  "specialty": "Cardiologia",
  "phone": "(11) 99999-0001",
  "active": true
}
```

---

### 5.3 Pacientes

| Método | Endpoint | Autenticação | Descrição |
|--------|----------|--------------|-----------|
| POST | `/patients` | ADMIN, RECEPCIONISTA | Cadastrar paciente |
| GET | `/patients` | Autenticado | Listar ativos (filtro: `?name=Ana`) |
| GET | `/patients/{id}` | Autenticado | Buscar por ID |
| PUT | `/patients/{id}` | ADMIN, RECEPCIONISTA | Atualizar |
| DELETE | `/patients/{id}` | ADMIN | Desativar (soft delete) |

#### POST `/patients`
```json
// Request
{
  "name": "Ana Paula Silva",
  "cpf": "123.456.789-00",
  "email": "ana@email.com",
  "birthDate": "1990-05-15",
  "phone": "(11) 98888-1234",
  "emergencyContact": "Pedro Silva - (11) 97777-9999",
  "bloodType": "A_POS",
  "allergies": "Dipirona",
  "insuranceProvider": "Amil",
  "insuranceNumber": "98765432"
}
```

---

### 5.4 Consultas

| Método | Endpoint | Autenticação | Descrição |
|--------|----------|--------------|-----------|
| POST | `/appointments` | Autenticado | Agendar consulta |
| GET | `/appointments` | Autenticado | Listar (filtros: `doctorId`, `patientId`, `status`, `from`, `to`) |
| GET | `/appointments/{id}` | Autenticado | Buscar por ID |
| PUT | `/appointments/{id}/status` | Autenticado | Atualizar status |
| DELETE | `/appointments/{id}` | Autenticado | Cancelar (`?reason=motivo`) |

#### POST `/appointments`
```json
// Request
{
  "doctorId": 1,
  "patientId": 1,
  "dateTime": "2026-06-01T09:00:00",
  "paymentType": "PARTICULAR",
  "notes": "Consulta de rotina"
}
```

#### PUT `/appointments/{id}/status`
```json
// Request
{ "status": "CONFIRMADA" }

// Para cancelar:
{ "status": "CANCELADA", "cancellationReason": "Paciente desmarcou." }
```

---

### 5.5 Prontuários Eletrônicos

| Método | Endpoint | Autenticação | Descrição |
|--------|----------|--------------|-----------|
| POST | `/medical-records` | ADMIN, MEDICO | Criar prontuário (consulta deve ser `REALIZADA`) |
| GET | `/medical-records/{id}` | ADMIN, MEDICO | Buscar por ID |
| GET | `/medical-records/patient/{patientId}` | ADMIN, MEDICO | Histórico do paciente |

#### POST `/medical-records`
```json
// Request
{
  "appointmentId": 1,
  "anamnesis": "Paciente relata dor no peito há 2 dias.",
  "diagnosis": "Angina instável suspeita.",
  "cidCode": "I20.0",
  "prescription": "AAS 100mg 1x/dia. Repouso relativo.",
  "evolution": "Paciente estável. Solicitar ECG."
}
```

---

## 6. Segurança

### 6.1 Autenticação JWT

- **Algoritmo:** HS256
- **Expiração padrão:** 24 horas (86400000 ms)
- **Header:** `Authorization: Bearer <token>`

### 6.2 Controle de Acesso (RBAC)

| Role | Permissões |
|------|-----------|
| `ADMIN` | Acesso total a todos os endpoints |
| `MEDICO` | Consultas (leitura/escrita), Prontuários (leitura/escrita), Pacientes (leitura) |
| `RECEPCIONISTA` | Pacientes (criar/editar), Consultas (criar/editar/cancelar), Doutores (leitura) |

### 6.3 Regras de Negócio de Segurança

1. **Inatividade:** Usuários com `active = false` não conseguem autenticar
2. **Token:** Armazenado apenas no header — nunca em cookie ou URL
3. **BCrypt:** Todas as senhas usam BCrypt com fator 12
4. **Segredo JWT:** Deve ser definido via variável de ambiente `JWT_SECRET` em produção

---

## 7. Regras de Negócio

### 7.1 Médicos
- CRM deve ser único no sistema
- E-mail deve ser único no sistema
- Exclusão é **soft delete** — nunca remove do banco
- Médico inativo não pode receber novos agendamentos

### 7.2 Pacientes
- CPF deve ser único no sistema
- Pacientes **não possuem conta de usuário** — são gerenciados por recepcionistas/admin
- Exclusão é **soft delete**

### 7.3 Consultas
- **Conflito de horário:** um médico não pode ter duas consultas ativas (não canceladas) no mesmo horário → retorna `409 Conflict`
- **Status imutável:** consulta `CANCELADA` ou `REALIZADA` não pode ter status alterado
- **Motivo obrigatório:** ao cancelar, o campo `cancellationReason` é obrigatório
- Agendamento só é permitido para médicos e pacientes **ativos**

### 7.4 Prontuários
- Só pode ser criado para consultas com status `REALIZADA`
- Cada consulta pode ter **no máximo um** prontuário → `409 Conflict` se já existir
- Após criação, prontuários são imutáveis (somente leitura)

---

## 8. Tratamento de Erros

Todos os erros retornam o formato padronizado:

```json
{
  "status": 400,
  "message": "CPF já cadastrado: 123.456.789-00",
  "timestamp": "2026-05-03T10:30:00"
}
```

| Código | Situação |
|--------|----------|
| 400 Bad Request | Validação de campos / regra de negócio |
| 401 Unauthorized | Token ausente, inválido ou expirado |
| 403 Forbidden | Permissão insuficiente para o endpoint |
| 404 Not Found | Recurso não encontrado por ID |
| 409 Conflict | Conflito de horário / prontuário duplicado |
| 500 Internal Error | Erro inesperado no servidor |

---

## 9. Como Executar

### 9.1 Pré-requisitos

- Java 21+ (ou JDK 25 com `--release 21`)
- Docker Desktop 4.x+
- Git

> **Nota:** O projeto foi criado com Java 21 como target. Caso use JDK 25 EA, o projeto compilará normalmente usando `--release 21`.

### 9.2 Subir o banco com Docker

```bash
cd Clinic-facul
docker-compose up -d

# Verificar se os containers estão saudáveis
docker ps
```

Aguarde o container `crm_postgres` ficar `healthy` antes de subir o backend.

### 9.3 Subir o backend

```bash
cd backend

# Copie e edite as variáveis de ambiente (opcional para dev)
cp ../.env.example .env

# Compilar e executar
./mvnw spring-boot:run
```

Na primeira execução, o Flyway cria automaticamente as tabelas e o `DataInitializer` cria o usuário admin:

```
E-mail: admin@axislab.com
Senha:  Admin@123
```

### 9.4 Acessar a documentação interativa

```
Swagger UI:  http://localhost:8080/swagger-ui.html
API Docs:    http://localhost:8080/v3/api-docs
pgAdmin:     http://localhost:5050  (admin@axislab.com / admin123)
```

---

## 10. Configuração do Postman

1. Abra o Postman → **New Collection** → `CRM Médico API`
2. Crie uma **Environment** `Local`:
   - `base_url` = `http://localhost:8080/api/v1`
   - `token` = (preencher após login)
3. Em todas as requisições autenticadas, adicione o header:
   ```
   Authorization: Bearer {{token}}
   ```

### Fluxo de teste recomendado

```
1. POST /auth/login              → copiar token
2. POST /doctors                 → criar médico (usar token ADMIN)
3. POST /patients                → criar paciente
4. POST /appointments            → agendar consulta
5. PUT  /appointments/{id}/status { "status": "REALIZADA" }
6. POST /medical-records         → criar prontuário
7. GET  /medical-records/patient/{id} → ver histórico
```

---

## 11. Testes de Erro (Casos Negativos)

| Cenário | Request | Resposta Esperada |
|---------|---------|------------------|
| E-mail duplicado | POST /doctors com email existente | 400 — E-mail já cadastrado |
| CRM duplicado | POST /doctors com CRM existente | 400 — CRM já cadastrado |
| CPF duplicado | POST /patients com CPF existente | 400 — CPF já cadastrado |
| Token ausente | GET /patients sem header | 401 Unauthorized |
| Token expirado | GET /patients com token antigo | 401 Unauthorized |
| Acesso negado | POST /doctors como RECEPCIONISTA | 403 Forbidden |
| Recurso inexistente | GET /doctors/9999 | 404 Not Found |
| Conflito de horário | POST /appointments mesmo médico/hora | 409 Conflict |
| Prontuário duplicado | POST /medical-records mesmo appointment | 409 Conflict |
| Consulta não realizada | POST /medical-records em consulta AGENDADA | 400 Bad Request |

---

## 12. Variáveis de Ambiente

| Variável | Padrão | Descrição |
|----------|--------|-----------|
| `DB_HOST` | `localhost` | Host do PostgreSQL |
| `DB_PORT` | `5432` | Porta do PostgreSQL |
| `DB_NAME` | `crm_medico` | Nome do banco |
| `DB_USER` | `crm_user` | Usuário do banco |
| `DB_PASSWORD` | `crm_pass` | Senha do banco |
| `JWT_SECRET` | (valor dev) | Segredo para assinar tokens JWT |
| `SERVER_PORT` | `8080` | Porta da aplicação |

> **Produção:** nunca use os valores padrão. Defina `JWT_SECRET` como uma string aleatória com 64+ caracteres.

---

## 13. Roadmap — Próximas Funcionalidades (Parte 3)

| Funcionalidade | Prioridade | Tecnologia |
|----------------|-----------|-----------|
| Frontend React completo | Alta | React + Tailwind + AuthContext |
| Dashboard com métricas | Alta | Recharts + Spring `/dashboard` endpoint |
| Calendário de consultas | Alta | FullCalendar |
| Tela de prontuário | Alta | React Hook Form |
| Upload de exames | Média | Spring Multipart + MinIO |
| Notificação por e-mail | Média | Spring Mail |
| Testes de integração | Alta | JUnit 5 + Mockito + TestContainers |
| Pipeline CI/CD | Média | GitHub Actions + Docker Hub |
| Deploy em cloud | Baixa | Railway ou AWS EC2 |

---

## 14. Convenções do Projeto

- **DTOs:** Java Records (imutáveis) — um por camada (request/response)
- **Mapeamento:** factory method `DTO.from(entity)` — sem MapStruct para simplicidade
- **Transações:** `@Transactional` nos services; `readOnly = true` em leituras
- **Paginação:** todos os endpoints de listagem retornam `Page<T>` com `Pageable`
- **Soft Delete:** entidades nunca são removidas fisicamente — apenas `active = false`
- **Validação:** Bean Validation nos campos do request DTO + regras de negócio no service
- **Exceções:** hierarquia clara — `BusinessException`, `ResourceNotFoundException`, `ConflictException`

---

*AXIS Lab · 2026 · Documento Confidencial*
