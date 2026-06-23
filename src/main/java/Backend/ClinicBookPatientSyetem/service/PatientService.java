package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.model.dto.request.PatientRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.PatientResponse;

public interface PatientService {

    PatientResponse create(PatientRequest request);
}
