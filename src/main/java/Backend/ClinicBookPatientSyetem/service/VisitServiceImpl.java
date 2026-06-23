package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.exception.BusinessRuleException;
import Backend.ClinicBookPatientSyetem.exception.ResourceNotFoundException;
import Backend.ClinicBookPatientSyetem.model.dto.request.VisitRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.VisitResponse;
import Backend.ClinicBookPatientSyetem.model.entity.Appointment;
import Backend.ClinicBookPatientSyetem.model.entity.Visit;
import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import Backend.ClinicBookPatientSyetem.repository.AppointmentRepository;
import Backend.ClinicBookPatientSyetem.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public VisitResponse record(Long appointmentId, VisitRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + appointmentId));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Cannot record visit for a cancelled appointment");
        }

        LocalDateTime slotDateTime = appointment.getSlot().getStartTime();

        if (slotDateTime.isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException(
                    "Cannot record visit for a future appointment. Slot starts at: " + slotDateTime);
        }

        if (visitRepository.existsByAppointmentId(appointmentId)) {
            throw new BusinessRuleException(
                    "Visit already recorded for this appointment");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        Visit visit = visitRepository.save(Visit.builder()
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .prescription(request.getPrescription())
                .recordedAt(LocalDateTime.now())
                .build());

        return toResponse(visit);
    }

    private VisitResponse toResponse(Visit visit) {
        Appointment a = visit.getAppointment();
        return VisitResponse.builder()
                .visitId(visit.getId())
                .appointmentId(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getName())
                .doctorId(a.getSlot().getDoctor().getId())
                .doctorName(a.getSlot().getDoctor().getName())
                .slotDate(a.getSlot().getSlotDate())
                .startTime(a.getSlot().getStartTime())
                .diagnosis(visit.getDiagnosis())
                .prescription(visit.getPrescription())
                .recordedAt(visit.getRecordedAt())
                .build();
    }
}
