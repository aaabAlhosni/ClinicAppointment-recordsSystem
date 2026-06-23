package Backend.ClinicBookPatientSyetem.service;

import Backend.ClinicBookPatientSyetem.model.dto.response.ScheduleResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.SlotResponse;

import java.time.LocalDate;
import java.util.List;

public interface SlotService {

    List<SlotResponse> generateSlots(Long doctorId, LocalDate date);

    ScheduleResponse getSchedule(Long doctorId, LocalDate date);
}
