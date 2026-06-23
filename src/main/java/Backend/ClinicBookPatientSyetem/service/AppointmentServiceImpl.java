package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.exception.BusinessRuleException;
import Backend.ClinicBookPatientSyetem.exception.ResourceNotFoundException;
import Backend.ClinicBookPatientSyetem.model.dto.request.BookingRequest;
import Backend.ClinicBookPatientSyetem.model.dto.request.RescheduleRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.AppointmentResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.RescheduleResponse;
import Backend.ClinicBookPatientSyetem.model.entity.Appointment;
import Backend.ClinicBookPatientSyetem.model.entity.AppointmentSlot;
import Backend.ClinicBookPatientSyetem.model.entity.Patient;
import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import Backend.ClinicBookPatientSyetem.repository.AppointmentRepository;
import Backend.ClinicBookPatientSyetem.repository.AppointmentSlotRepository;
import Backend.ClinicBookPatientSyetem.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentSlotRepository slotRepository;

    // ── Book ──────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AppointmentResponse book(BookingRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + request.getPatientId()));

        AppointmentSlot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment slot not found with id: " + request.getSlotId()));

        // Rule 1 — no double-booking: slot must not have an active BOOKED appointment.
        if (appointmentRepository.existsBySlotIdAndStatus(slot.getId(), AppointmentStatus.BOOKED)) {
            throw new BusinessRuleException("Slot is already booked");
        }

        // Rule 2 — no same-day duplicate: patient cannot have two BOOKED appointments
        // with the same doctor on the same calendar day.
        if (appointmentRepository.existsActiveBookingForPatientDoctorDay(
                patient.getId(), slot.getDoctor().getId(), slot.getSlotDate())) {
            throw new BusinessRuleException(
                    "Patient already has an active appointment with this doctor today");
        }

        Appointment saved = appointmentRepository.save(Appointment.builder()
                .patient(patient)
                .slot(slot)
                .status(AppointmentStatus.BOOKED)
                .bookedAt(LocalDateTime.now())
                .build());

        return toResponse(saved);
    }

    // ── Cancel ────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public AppointmentResponse cancel(Long appointmentId) {
        Appointment appointment = findById(appointmentId);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessRuleException(
                    "Cannot cancel an appointment that is not BOOKED. Current status: "
                    + appointment.getStatus());
        }

        // Status change only — the row is NEVER deleted.
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return toResponse(appointmentRepository.save(appointment));
    }

    // ── Reschedule ────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public RescheduleResponse reschedule(Long appointmentId, RescheduleRequest request) {
        Appointment old = findById(appointmentId);

        if (old.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessRuleException(
                    "Can only reschedule a BOOKED appointment. Current status: "
                    + old.getStatus());
        }

        AppointmentSlot newSlot = slotRepository.findById(request.getNewSlotId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment slot not found with id: " + request.getNewSlotId()));

        if (appointmentRepository.existsBySlotIdAndStatus(newSlot.getId(), AppointmentStatus.BOOKED)) {
            throw new BusinessRuleException("Target slot is already booked");
        }

        // Create the new BOOKED appointment row first.
        Appointment newAppt = appointmentRepository.save(Appointment.builder()
                .patient(old.getPatient())
                .slot(newSlot)
                .status(AppointmentStatus.BOOKED)
                .bookedAt(LocalDateTime.now())
                .build());

        // Mark the original RESCHEDULED and link it to the new row — NEVER delete.
        old.setStatus(AppointmentStatus.RESCHEDULED);
        old.setRescheduledTo(newAppt);
        appointmentRepository.save(old);

        return RescheduleResponse.builder()
                .original(toResponse(old))
                .rescheduled(toResponse(newAppt))
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + id));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getName())
                .doctorId(a.getSlot().getDoctor().getId())
                .doctorName(a.getSlot().getDoctor().getName())
                .slotId(a.getSlot().getId())
                .slotDate(a.getSlot().getSlotDate())
                .startTime(a.getSlot().getStartTime())
                .endTime(a.getSlot().getEndTime())
                .status(a.getStatus())
                .bookedAt(a.getBookedAt())
                .rescheduledToId(a.getRescheduledTo() != null ? a.getRescheduledTo().getId() : null)
                .build();
    }
}
