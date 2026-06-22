package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.exception.ResourceNotFoundException;
import Backend.ClinicBookPatientSyetem.model.dto.request.PatientRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.PatientResponse;
import Backend.ClinicBookPatientSyetem.model.entity.Patient;
import Backend.ClinicBookPatientSyetem.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public PatientResponse create(PatientRequest request) {
        Patient patient = Patient.builder()
                .name(request.getName())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .build();

        return toResponse(patientRepository.save(patient));
    }

    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
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
