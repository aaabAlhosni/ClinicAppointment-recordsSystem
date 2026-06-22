package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.exception.ResourceNotFoundException;
import Backend.ClinicBookPatientSyetem.model.dto.request.DoctorRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.DoctorResponse;
import Backend.ClinicBookPatientSyetem.model.entity.Doctor;
import Backend.ClinicBookPatientSyetem.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    @Override
    public DoctorResponse create(DoctorRequest request) {
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
