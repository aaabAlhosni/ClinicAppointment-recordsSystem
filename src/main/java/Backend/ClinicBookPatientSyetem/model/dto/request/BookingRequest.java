package Backend.ClinicBookPatientSyetem.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;
}
