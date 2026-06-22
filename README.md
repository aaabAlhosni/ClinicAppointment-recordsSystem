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
