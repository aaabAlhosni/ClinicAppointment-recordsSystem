package Backend.ClinicBookPatientSyetem.controller;

import Backend.ClinicBookPatientSyetem.model.dto.response.ScheduleResponse;
import Backend.ClinicBookPatientSyetem.model.dto.response.SlotResponse;
import Backend.ClinicBookPatientSyetem.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping("/slots")
    public ResponseEntity<List<SlotResponse>> generateSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.generateSlots(doctorId, date));
    }

    @GetMapping("/schedule")
    public ResponseEntity<ScheduleResponse> getSchedule(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(slotService.getSchedule(doctorId, date));
    }
}
