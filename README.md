# Clinic Appointment & Patient Records API

> A production-quality RESTful backend API for managing an outpatient clinic — doctors, patients, appointment slots, and visit records — with full audit history and strict business rule enforcement.

---

## About the Project

This is a RESTful backend API for a small outpatient clinic built with Java 17 and Spring Boot 3.x. The system manages doctors, patients, appointment slots, and visit records. It handles booking, cancellation, rescheduling, and diagnosis recording while maintaining a complete, immutable audit trail through status-based modelling — cancelled and rescheduled appointments are never deleted, only updated with the appropriate status, preserving full history at all times.

---

## Aim

The aim is to design and deliver a production-quality REST API demonstrating mastery across system design, object-oriented domain modelling, relational database schema design, and RESTful API design. Appointment status (`BOOKED`, `CANCELLED`, `COMPLETED`, `RESCHEDULED`) is treated as a first-class domain concept, and rescheduling is modelled as a linked-record operation — a new appointment row is created while the original is marked `RESCHEDULED` — satisfying history, schedule, and audit requirements simultaneously without any data loss or row deletion.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Build Tool | Maven |
| Architecture | Layered REST API |

---

## Architecture

```
Controller  →  Service  →  Repository
   (HTTP)     (Business)    (Data Access)
```

Each layer has a single responsibility with no cross-layer leakage — controllers handle HTTP concerns only, services own all business logic, and repositories handle data access only. Every layer is independently testable via Spring dependency injection.

---

## Objectives

**Objective 1 — Database Design**
Design a normalised relational schema reaching 3NF, covering all five domain entities with proper primary keys, foreign key constraints, and a self-referencing `rescheduled_to` column on `Appointment` to model the rescheduling chain without row deletion.

**Objective 2 — Business Rule Enforcement**
Implement all business rules exclusively in the service layer: slot double-booking prevention, duplicate-active-appointment prevention for the same patient/doctor/day, and time-gated visit recording restricted to appointments whose scheduled start time has already passed.

**Objective 3 — RESTful API Design**
Expose a RESTful API mapping to real clinical operations, using correct HTTP status codes — `201` for creation, `404` for missing entities, `409` for business rule conflicts, `400` for validation failures — with structured JSON error responses that describe violations in human-readable terms.

**Objective 4 — Layered Architecture**
Apply a strict `Controller → Service → Repository` separation via Spring dependency injection, ensuring no business logic leaks into controllers and no raw queries reside in service classes, making each layer independently testable and replaceable without affecting the others.

**Objective 5 — Testing and Evidence**
Produce a Postman collection covering all nine API endpoints, including deliberate rule-violation requests for double-booking, same-day duplicates, and future-appointment visit recording, to prove that every enforcement mechanism returns the correct `HTTP 409` status with a descriptive error message for each specific violation.

---

## User Story

> As a receptionist or patient interacting with the clinic API, I need the system to manage the complete appointment lifecycle — from generating available slots, booking, rescheduling, and cancelling, through to recording diagnoses — while enforcing business rules that prevent double-booking and maintain a permanent, auditable history of every booking event so that both the doctor's schedule and the patient's medical record are always accurate and complete.

---

## Functional Requirements

| ID | Requirement |
|---|---|
| FR-01 | The system shall support creation of doctor records with name, specialty, and working hours defining available booking windows for appointment slot generation. |
| FR-02 | The system shall generate 30-minute appointment slots within a doctor's working hours for any specified date, with idempotent behaviour — already-generated slots are skipped to prevent duplicate entries. |
| FR-03 | The system shall prevent a slot with an active `BOOKED` appointment from being booked again; the slot becomes available only after the holding appointment is cancelled by the patient. |
| FR-04 | The system shall prevent a patient from holding two active non-cancelled appointments with the same doctor on the same calendar day, returning `HTTP 409` with a clear conflict explanation. |
| FR-05 | The system shall permit diagnosis and prescription recording only against appointments whose scheduled slot start time has already passed and whose status is not `CANCELLED`, enforcing that visit records reflect real clinical events. |

---

## Non-Functional Requirements

| ID | Requirement |
|---|---|
| NFR-01 | All write endpoints must validate input and return `HTTP 400` with a structured, descriptive JSON error body on failure — no raw Java stack traces are ever exposed to the API consumer. |
| NFR-02 | The API must use `201` for resource creation, `200` for reads, `404` for missing entities, and `409` for business rule conflicts, applied consistently across every endpoint in the system. |
| NFR-03 | Business logic must reside exclusively in the service layer; controllers handle HTTP concerns only and repositories handle data access only — no cross-layer responsibility leakage. |
| NFR-04 | All data must survive application restarts in a persistent SQL database with foreign key constraints enforced at the database level, not solely in application code or Hibernate mapping. |
| NFR-05 | The service layer must be independently unit-testable via Spring dependency injection without requiring a running HTTP server, live database, or full application context instantiation. |

---

## Before Running the Application

Before I start the server, I make sure the following are in place.

### Prerequisites

| Requirement | Version |
|---|---|
| Java (JDK) | 17 or later |
| Apache Maven | 3.8 or later |
| Oracle Database | 19c or later (default profile) |
| PostgreSQL | 14 or later (production profile — optional) |

### Step 1 — Configure the Database

Open `src/main/resources/application.yml` and fill in the three TODOs to point the app at your Oracle instance:

```yaml
datasource:
  url: jdbc:oracle:thin:@localhost:1521/ORCLPDB   # ← update host/port/SID
  username: ClinicSystem_db                         # ← your Oracle schema username
  password: your_password_here                      # ← your Oracle password
```

> **PostgreSQL alternative:** If I want to run against PostgreSQL instead, I use the production profile (see Step 3 below). I set three environment variables — `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` — and the prod config picks them up automatically.

### Step 2 — Build

```bash
mvn clean install -DskipTests
```

### Step 3 — Run

**Oracle (default):**
```bash
mvn spring-boot:run
```

**PostgreSQL (production profile):**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/clinicdb
export DB_USERNAME=my_user
export DB_PASSWORD=my_password
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

The API starts on **http://localhost:8080** with no context path prefix.

---

## API Endpoints

All endpoints are prefixed with `http://localhost:8080`. Dates use ISO-8601 format (`YYYY-MM-DD`); times use `HH:mm:ss`.

---

### Doctors

#### Create a Doctor
```
POST /api/doctors
```
I call this first to register a doctor before generating any slots.

**Request body:**
```json
{
  "name": "Dr. Sarah Malik",
  "specialty": "Cardiology",
  "workStart": "09:00:00",
  "workEnd": "17:00:00"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `name` | String | Yes | Must be unique per specialty |
| `specialty` | String | Yes | Must be unique per name |
| `workStart` | LocalTime | Yes | Must be within clinic hours (05:00–19:00) |
| `workEnd` | LocalTime | Yes | Must be after `workStart` |

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Dr. Sarah Malik",
  "specialty": "Cardiology",
  "workStart": "09:00:00",
  "workEnd": "17:00:00"
}
```

**Error cases:**
- `409 Conflict` — doctor with same name + specialty already exists
- `409 Conflict` — working hours are outside clinic hours (05:00–19:00)

---

### Slots

#### Generate Slots for a Doctor
```
POST /api/doctors/{doctorId}/slots?date=YYYY-MM-DD
```
I generate 30-minute appointment slots across the doctor's working hours for a given date. I must call this before any bookings can be made for that date.

**Response:** `201 Created`
```json
[
  {
    "id": 1,
    "doctorId": 1,
    "slotDate": "2026-06-25",
    "startTime": "2026-06-25T09:00:00",
    "endTime": "2026-06-25T09:30:00"
  },
  ...
]
```

**Error cases:**
- `409 Conflict` — slots have already been generated for this doctor on this date
- `400 Bad Request` — date is in the past

---

#### View Doctor's Daily Schedule
```
GET /api/doctors/{doctorId}/schedule?date=YYYY-MM-DD
```
I use this to see all slots for a doctor on a given day, with each slot showing whether it is `FREE` or `BOOKED`.

**Response:** `200 OK`
```json
{
  "doctorId": 1,
  "date": "2026-06-25",
  "slots": [
    {
      "slotId": 1,
      "startTime": "2026-06-25T09:00:00",
      "endTime": "2026-06-25T09:30:00",
      "status": "FREE"
    },
    {
      "slotId": 2,
      "startTime": "2026-06-25T09:30:00",
      "endTime": "2026-06-25T10:00:00",
      "status": "BOOKED"
    }
  ]
}
```

---

### Patients

#### Register a Patient
```
POST /api/patients
```

**Request body:**
```json
{
  "name": "Omar Al-Farsi",
  "dateOfBirth": "1990-03-15",
  "phone": "+971501234567"
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `name` | String | Yes | |
| `dateOfBirth` | LocalDate | Yes | Must be a past date |
| `phone` | String | Yes | |

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Omar Al-Farsi",
  "dateOfBirth": "1990-03-15",
  "phone": "+971501234567"
}
```

---

#### View Patient Appointment History
```
GET /api/patients/{id}/history
```
I use this to retrieve a full audit trail of all appointments (including cancelled and rescheduled ones) for a patient, along with any visit/diagnosis records attached.

**Response:** `200 OK`
```json
{
  "patientId": 1,
  "patientName": "Omar Al-Farsi",
  "appointments": [
    {
      "appointmentId": 1,
      "status": "RESCHEDULED",
      "bookedAt": "2026-06-23T10:00:00",
      "rescheduledToId": 2,
      "slotId": 1,
      "doctorId": 1,
      "doctorName": "Dr. Sarah Malik",
      "slotDate": "2026-06-25",
      "startTime": "2026-06-25T09:00:00",
      "endTime": "2026-06-25T09:30:00",
      "diagnosis": null,
      "prescription": null,
      "visitRecordedAt": null
    }
  ]
}
```

---

### Appointments

#### Book an Appointment
```
POST /api/appointments
```

**Request body:**
```json
{
  "patientId": 1,
  "slotId": 3
}
```

| Field | Type | Required | Notes |
|---|---|---|---|
| `patientId` | Long | Yes | Patient must exist |
| `slotId` | Long | Yes | Slot must exist and be FREE |

**Response:** `201 Created`
```json
{
  "id": 1,
  "patientId": 1,
  "patientName": "Omar Al-Farsi",
  "doctorId": 1,
  "doctorName": "Dr. Sarah Malik",
  "slotId": 3,
  "slotDate": "2026-06-25",
  "startTime": "2026-06-25T10:00:00",
  "endTime": "2026-06-25T10:30:00",
  "status": "BOOKED",
  "bookedAt": "2026-06-23T11:45:00",
  "rescheduledToId": null
}
```

**Error cases:**
- `409 Conflict` — slot is already booked
- `409 Conflict` — patient already has an active appointment with this doctor today

---

#### Cancel an Appointment
```
POST /api/appointments/{id}/cancel
```
I can cancel any appointment that is `BOOKED` or `RESCHEDULED`. Cancelling a `RESCHEDULED` appointment automatically cascades and cancels the linked new appointment as well — no active booking is left behind.

**Response:** `200 OK` — the cancelled appointment record

**Error cases:**
- `409 Conflict` — appointment is already `CANCELLED` or `COMPLETED`

---

#### Reschedule an Appointment
```
POST /api/appointments/{id}/reschedule
```
I can only reschedule a `BOOKED` appointment. The original appointment is marked `RESCHEDULED` and linked to a newly created `BOOKED` appointment in the target slot — no row is ever deleted.

**Request body:**
```json
{
  "newSlotId": 7
}
```

**Response:** `200 OK`
```json
{
  "original": {
    "id": 1,
    "status": "RESCHEDULED",
    "rescheduledToId": 2,
    ...
  },
  "rescheduled": {
    "id": 2,
    "status": "BOOKED",
    "rescheduledToId": null,
    ...
  }
}
```

**Error cases:**
- `409 Conflict` — appointment is not `BOOKED`
- `409 Conflict` — target slot is already booked

---

#### Record a Visit (Diagnosis)
```
POST /api/appointments/{id}/visit
```
I record a visit only after the appointment's scheduled start time has passed. The appointment must not be `CANCELLED`.

**Request body:**
```json
{
  "diagnosis": "Mild hypertension",
  "prescription": "Amlodipine 5mg once daily"
}
```

**Response:** `201 Created`
```json
{
  "visitId": 1,
  "appointmentId": 1,
  "patientId": 1,
  "patientName": "Omar Al-Farsi",
  "doctorId": 1,
  "doctorName": "Dr. Sarah Malik",
  "slotDate": "2026-06-25",
  "startTime": "2026-06-25T10:00:00",
  "diagnosis": "Mild hypertension",
  "prescription": "Amlodipine 5mg once daily",
  "recordedAt": "2026-06-25T10:45:00"
}
```

**Error cases:**
- `409 Conflict` — appointment slot start time has not passed yet (future appointment)
- `409 Conflict` — appointment is `CANCELLED`
- `409 Conflict` — visit already recorded for this appointment

---

## HTTP Status Code Reference

| Code | Meaning | When I see it |
|---|---|---|
| `200 OK` | Success (read or action) | GET requests, cancel, reschedule |
| `201 Created` | Resource created | POST for doctors, patients, appointments, slots, visits |
| `400 Bad Request` | Validation failure | Missing required fields, past date for slot generation |
| `404 Not Found` | Resource does not exist | Unknown doctor/patient/slot/appointment ID |
| `409 Conflict` | Business rule violation | Double-booking, duplicate doctor, cancelling a completed appointment, etc. |

---

## TODOs

### Configuration (open)

These three items in `src/main/resources/application.yml` must be updated before the application will connect to a database:

- [ ] **Line 6** — `url`: replace `@localhost:1521/ORCLPDB` with the correct Oracle host, port, and SID/service name for your instance
- [ ] **Line 8** — `username`: replace `ClinicSystem_db` with your Oracle schema username
- [ ] **Line 9** — `password`: replace the placeholder with your actual Oracle password

### Java Source (none)

There are currently no open TODO comments in the Java source files. All planned features for the current phase are implemented.

---

## Database Normalization — Final 3NF Schema

I started from a single flat relation containing every piece of appointment data and progressively decomposed it through First, Second, and Third Normal Form. The table below is the final result — the five-table schema used in the implementation.

In every table, every non-key attribute depends on the whole primary key and nothing but the primary key, satisfying the 3NF requirement. The self-referencing FK on `Appointment` (`rescheduled_to`) enables the full rescheduling chain without row deletion and without introducing any 3NF violation.

| Table | Primary Key | Foreign Keys | Remaining Attributes |
|---|---|---|---|
| **Doctor** | `doctor_id` | — | `name`, `specialty`, `work_start`, `work_end` |
| **Patient** | `patient_id` | — | `name`, `date_of_birth`, `phone` |
| **AppointmentSlot** | `slot_id` | `doctor_id → Doctor` | `slot_date`, `start_time`, `end_time` |
| **Appointment** | `appointment_id` | `patient_id → Patient`<br>`slot_id → AppointmentSlot`<br>`rescheduled_to → Appointment` | `status`, `booked_at` |
| **Visit** | `visit_id` | `appointment_id → Appointment` (UNIQUE) | `diagnosis`, `prescription`, `recorded_at` |
