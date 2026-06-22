package Backend.ClinicBookPatientSyetem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {

    private Long doctorId;
    private LocalDate date;
    private List<SlotEntry> slots;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SlotEntry {
        private Long slotId;
        private LocalTime startTime;
        private LocalTime endTime;
        private String status;  // "FREE" or "BOOKED"
    }
}
