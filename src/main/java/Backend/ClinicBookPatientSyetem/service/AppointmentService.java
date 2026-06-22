package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.model.dto.request.BookingRequest;
import Backend.ClinicBookPatientSyetem.model.dto.request.RescheduleRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.AppointmentResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.RescheduleResponse;

public interface AppointmentService {

    AppointmentResponse book(BookingRequest request);

    AppointmentResponse cancel(Long appointmentId);

    RescheduleResponse reschedule(Long appointmentId, RescheduleRequest request);
}
