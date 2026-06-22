package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.model.dto.request.VisitRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.VisitResponse;

public interface VisitService {

    VisitResponse record(Long appointmentId, VisitRequest request);
}
