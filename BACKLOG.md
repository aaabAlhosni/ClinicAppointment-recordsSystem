# Clinic Booking System — Project Backlog

---

## Version 0.1 — Foundation & Domain Model

**Status:** In Progress

### What this version includes:

1. **Project bootstrap** — I set up the project and connected it to a local database.
2. **Core entities** — I defined the main building blocks: Patient, Doctor, Appointment, Slot, and Visit.
3. **Appointment status** — I track whether an appointment is scheduled, completed, or cancelled.
4. **Error handling** — I made the system return clear messages when something goes wrong.
5. **Database console** — I can inspect and view all data directly in the browser during development.
6. **Oracle-ready** — I prepared the config so switching to a real production database is straightforward.

---

## Version 0.2 — Service & API Layer (Doctor + Patient)

**Status:** In Progress

### What this version includes:

1. **Request & response DTOs** — I shaped what data comes in and goes out for Doctor and Patient.
2. **Validation on input** — I made sure required fields are checked before anything is saved.
3. **Repository layer** — I connected Doctor and Patient to the database with ready-made CRUD operations.
4. **Service layer** — I wrote the business logic that sits between the API and the database.
5. **POST endpoints** — I can now create a new Doctor or Patient through the API.
6. **404 on missing records** — I made the system respond clearly when a Doctor or Patient is not found.

---

## Version 0.3 — Full CRUD REST Controllers (Doctor + Patient)

**Status:** In Progress

### What this version includes:

1. **GET by ID** — I can fetch a single Doctor or Patient by their ID.
2. **GET all** — I can retrieve the full list of Doctors or Patients at once.
3. **PUT update** — I can update an existing Doctor or Patient record through the API.
4. **DELETE** — I can remove a Doctor or Patient and the system confirms it cleanly.
5. **End-to-end verification** — I tested the full cycle: create, read, update, and delete via API.
6. **H2 console validation** — I confirmed all changes reflect correctly in the database browser.

---

## Version 0.4 — Oracle Local Database Migration

**Status:** Completed

### What this version includes:

1. **Oracle JDBC driver** — I added the `ojdbc11` dependency so the app can connect to a real Oracle database.
2. **H2 scoped to test** — I moved H2 to test scope only, removing it from the runtime to avoid conflicts with Oracle.
3. **Oracle datasource config** — I replaced the H2 connection settings in `application.yml` with Oracle driver, URL, and credentials.
4. **Oracle dialect** — I switched the Hibernate dialect from `H2Dialect` to `OracleDialect` so SQL is generated correctly for Oracle.
5. **H2 console removed** — I cleaned out the H2 browser console config since it no longer applies to an Oracle-backed setup.
6. **Schema auto-creation on Oracle** — Hibernate now runs against Oracle, auto-generating all five tables in the connected schema.

---

## Version 0.5 — Doctor Clinic Hours Validation

**Status:** In Progress

### What this version includes:

1. **Day-shift enforcement** — I restricted doctor creation to clinic hours only: work start cannot be before 05:00 and work end cannot be after 19:00.
2. **Work window integrity check** — I added a rule that ensures work start time must always be strictly before work end time.
3. **Service-layer validation** — I placed both rules in `DoctorServiceImpl` as `BusinessRuleException` throws, consistent with existing duplicate-doctor validation.
4. **Clear error messages** — Each violation returns a descriptive message so the API caller knows exactly which rule was broken.
5. **Night-shift rejection** — Any attempt to register a doctor with evening or overnight hours is now explicitly blocked at the business logic level.
