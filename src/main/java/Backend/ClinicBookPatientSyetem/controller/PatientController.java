package Backend.ClinicBookPatientSyetem.controller;

import Backend.ClinicBookPatientSyetem.model.dto.request.PatientRequest;
import Backend.ClinicBookPatientSyetem.model.dto.response.PatientHistoryResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.PatientResponse;
import Backend.ClinicBookPatientSyetem.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> create(@Valid @RequestBody PatientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(request));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<PatientHistoryResponse> getHistory(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getHistory(id));
    }
}
