package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.exception.BusinessRuleException;
import Backend.ClinicBookPatientSyetem.exception.InvalidRequestException;
import Backend.ClinicBookPatientSyetem.exception.ResourceNotFoundException;
import Backend.ClinicBookPatientSyetem.model.dto.response.ScheduleResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.SlotResponse;
import Backend.ClinicBookPatientSyetem.model.entity.AppointmentSlot;
import Backend.ClinicBookPatientSyetem.model.entity.Doctor;
import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import Backend.ClinicBookPatientSyetem.repository.AppointmentRepository;
import Backend.ClinicBookPatientSyetem.repository.AppointmentSlotRepository;
import Backend.ClinicBookPatientSyetem.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final AppointmentSlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public List<SlotResponse> generateSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        if (date.isBefore(LocalDate.now())) {
            throw new InvalidRequestException("Slot generation date cannot be in the past: " + date);
        }

        if (slotRepository.existsByDoctorIdAndSlotDate(doctorId, date)) {
            throw new BusinessRuleException(
                    "Slots have already been generated for doctor " + doctorId + " on " + date);
        }

        LocalTime current = doctor.getWorkStart();
        while (!current.plusMinutes(30).isAfter(doctor.getWorkEnd())) {
            slotRepository.save(AppointmentSlot.builder()
                    .doctor(doctor)
                    .slotDate(date)
                    .startTime(LocalDateTime.of(date, current))
                    .endTime(LocalDateTime.of(date, current.plusMinutes(30)))
                    .build());
            current = current.plusMinutes(30);
        }

        return slotRepository.findAllByDoctorIdAndSlotDateOrderByStartTime(doctorId, date)
                .stream()
                .map(this::toSlotResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleResponse getSchedule(Long doctorId, LocalDate date) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }

        List<ScheduleResponse.SlotEntry> entries = slotRepository
                .findAllByDoctorIdAndSlotDateOrderByStartTime(doctorId, date)
                .stream()
                .map(slot -> ScheduleResponse.SlotEntry.builder()
                        .slotId(slot.getId())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .status(appointmentRepository.existsBySlotIdAndStatus(slot.getId(), AppointmentStatus.BOOKED)
                                ? "BOOKED" : "FREE")
                        .build())
                .toList();

        return ScheduleResponse.builder()
                .doctorId(doctorId)
                .date(date)
                .slots(entries)
                .build();
    }

    // Reusable slot lookup for AppointmentService (Phase 4).
    public AppointmentSlot findSlotById(Long slotId) {
        return slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment slot not found with id: " + slotId));
    }

    private SlotResponse toSlotResponse(AppointmentSlot slot) {
        return SlotResponse.builder()
                .id(slot.getId())
                .doctorId(slot.getDoctor().getId())
                .slotDate(slot.getSlotDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .build();
    }
}
