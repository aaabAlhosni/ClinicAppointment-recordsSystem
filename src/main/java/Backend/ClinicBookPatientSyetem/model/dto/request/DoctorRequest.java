package Backend.ClinicBookPatientSyetem.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class DoctorRequest {

    @NotBlank(message = "Doctor name is required")
    private String name;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    @NotNull(message = "Work start time is required")
    private LocalTime workStart;

    @NotNull(message = "Work end time is required")
    private LocalTime workEnd;
}
