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
public class SlotResponse {

    private Long id;
    private Long doctorId;
    private LocalDate slotDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
