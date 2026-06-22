package Backend.ClinicBookPatientSyetem.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RescheduleRequest {

    @NotNull(message = "New slot ID is required")
    private Long newSlotId;
}
