package Backend.ClinicBookPatientSyetem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitResponse {

    private Long visitId;
    private Long appointmentId;

    private Long patientId;
    private String patientName;

    private Long doctorId;
    private String doctorName;

    private LocalDate slotDate;
    private LocalDateTime startTime;

    private String diagnosis;
    private String prescription;
    private LocalDateTime recordedAt;
}
