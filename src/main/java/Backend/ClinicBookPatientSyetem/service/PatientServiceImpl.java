package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.exception.ResourceNotFoundException;
import Backend.ClinicBookPatientSyetem.model.dto.request.PatientRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.PatientHistoryResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.PatientResponse;
import Backend.ClinicBookPatientSyetem.model.entity.Appointment;
import Backend.ClinicBookPatientSyetem.model.entity.Patient;
import Backend.ClinicBookPatientSyetem.model.entity.Visit;
import Backend.ClinicBookPatientSyetem.repository.AppointmentRepository;
import Backend.ClinicBookPatientSyetem.repository.PatientRepository;
import Backend.ClinicBookPatientSyetem.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final VisitRepository visitRepository;

    @Override
    @Transactional
    public PatientResponse create(PatientRequest request) {
        Patient patient = Patient.builder()
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .build();
        return toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientHistoryResponse getHistory(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + patientId));

        List<Appointment> appointments =
                appointmentRepository.findAllByPatientIdOrderByBookedAtDesc(patientId);

        List<PatientHistoryResponse.AppointmentEntry> entries = appointments.stream()
                .map(a -> {
                    Optional<Visit> visit = visitRepository.findByAppointmentId(a.getId());
                    return PatientHistoryResponse.AppointmentEntry.builder()
                            .appointmentId(a.getId())
                            .status(a.getStatus())
                            .bookedAt(a.getBookedAt())
                            .rescheduledToId(a.getRescheduledTo() != null
                                    ? a.getRescheduledTo().getId() : null)
                            .slotId(a.getSlot().getId())
                            .doctorId(a.getSlot().getDoctor().getId())
                            .doctorName(a.getSlot().getDoctor().getName())
                            .slotDate(a.getSlot().getSlotDate())
                            .startTime(a.getSlot().getStartTime())
                            .endTime(a.getSlot().getEndTime())
                            .diagnosis(visit.map(Visit::getDiagnosis).orElse(null))
                            .prescription(visit.map(Visit::getPrescription).orElse(null))
                            .visitRecordedAt(visit.map(Visit::getRecordedAt).orElse(null))
                            .build();
                })
                .toList();

        return PatientHistoryResponse.builder()
                .patientId(patient.getId())
                .patientName(patient.getName())
                .appointments(entries)
                .build();
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with id: " + id));
    }

    private PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getName())
                .dateOfBirth(patient.getDateOfBirth())
                .phone(patient.getPhone())
                .build();
    }
}
