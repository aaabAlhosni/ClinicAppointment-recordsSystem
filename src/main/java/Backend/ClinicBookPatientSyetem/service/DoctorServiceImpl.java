package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.exception.BusinessRuleException;
import Backend.ClinicBookPatientSyetem.exception.ResourceNotFoundException;
import Backend.ClinicBookPatientSyetem.model.dto.request.DoctorRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.DoctorResponse;
import Backend.ClinicBookPatientSyetem.model.entity.Doctor;
import Backend.ClinicBookPatientSyetem.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    private static final LocalTime CLINIC_OPEN  = LocalTime.of(5, 0);
    private static final LocalTime CLINIC_CLOSE = LocalTime.of(19, 0);

    @Override
    public DoctorResponse create(DoctorRequest request) {
        if (request.getWorkStart().isBefore(CLINIC_OPEN) || request.getWorkEnd().isAfter(CLINIC_CLOSE)) {
            throw new BusinessRuleException(
                    "Doctor working hours must be within clinic hours (05:00 – 19:00)");
        }
        if (!request.getWorkStart().isBefore(request.getWorkEnd())) {
            throw new BusinessRuleException(
                    "Work start time must be before work end time");
        }
        if (doctorRepository.existsByNameAndSpecialty(request.getName(), request.getSpecialty())) {
            throw new BusinessRuleException(
                    "A doctor with name '" + request.getName() +
                    "' and specialty '" + request.getSpecialty() + "' already exists");
        }

        Doctor doctor = Doctor.builder()
                .name(request.getName())
                .specialty(request.getSpecialty())
                .workStart(request.getWorkStart())
                .workEnd(request.getWorkEnd())
                .build();

        return toResponse(doctorRepository.save(doctor));
    }

    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    private DoctorResponse toResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialty(doctor.getSpecialty())
                .workStart(doctor.getWorkStart())
                .workEnd(doctor.getWorkEnd())
                .build();
    }
}
