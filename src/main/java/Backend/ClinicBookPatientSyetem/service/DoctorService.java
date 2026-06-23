package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.model.dto.request.DoctorRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.DoctorResponse;

public interface DoctorService {

    DoctorResponse create(DoctorRequest request);
}
