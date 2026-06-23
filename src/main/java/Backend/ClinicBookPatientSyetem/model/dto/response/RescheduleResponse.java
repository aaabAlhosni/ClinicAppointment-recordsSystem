package Backend.ClinicBookPatientSyetem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RescheduleResponse {

    private AppointmentResponse original;       // status = RESCHEDULED, rescheduledToId populated
    private AppointmentResponse rescheduled;    // new BOOKED appointment
}
