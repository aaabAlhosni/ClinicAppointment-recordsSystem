package Backend.ClinicBookPatientSyetem.controller;

import Backend.ClinicBookPatientSyetem.model.dto.request.BookingRequest;
import Backend.ClinicBookPatientSyetem.model.dto.request.RescheduleRequest;
import Backend.ClinicBookPatientSyetem.model.dto.request.VisitRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.AppointmentResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.RescheduleResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.VisitResponse;
import Backend.ClinicBookPatientSyetem.service.AppointmentService;
import Backend.ClinicBookPatientSyetem.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final VisitService visitService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.book(request));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancel(id));
    }

    @PostMapping("/{id}/reschedule")
    public ResponseEntity<RescheduleResponse> reschedule(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(appointmentService.reschedule(id, request));
    }

    @PostMapping("/{id}/visit")
    public ResponseEntity<VisitResponse> recordVisit(
            @PathVariable Long id,
            @Valid @RequestBody VisitRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(visitService.record(id, request));
    }
}
