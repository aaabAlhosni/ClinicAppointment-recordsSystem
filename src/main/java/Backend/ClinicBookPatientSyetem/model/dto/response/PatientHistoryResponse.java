package Backend.ClinicBookPatientSyetem.model.dto.response;

import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientHistoryResponse {

    private Long patientId;
    private String patientName;
    private List<AppointmentEntry> appointments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppointmentEntry {

        private Long appointmentId;
        private AppointmentStatus status;
        private LocalDateTime bookedAt;
        private Long rescheduledToId;

        private Long slotId;
        private Long doctorId;
        private String doctorName;
        private LocalDate slotDate;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        private String diagnosis;
        private String prescription;
        private LocalDateTime visitRecordedAt;
    }
}
