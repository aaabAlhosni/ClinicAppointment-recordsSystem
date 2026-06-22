package Backend.ClinicBookPatientSyetem.model.dto.response;

import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private Long id;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private Long slotId;
    private LocalDate slotDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private AppointmentStatus status;
    private LocalDateTime bookedAt;

    private Long rescheduledToId;   // null unless this appointment was rescheduled
}
