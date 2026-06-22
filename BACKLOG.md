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
