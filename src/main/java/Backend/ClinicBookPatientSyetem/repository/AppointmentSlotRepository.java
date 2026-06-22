package Backend.ClinicBookPatientSyetem.repository;

import Backend.ClinicBookPatientSyetem.model.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    // Idempotency check: skip slot creation if it already exists for this doctor/date/time window.
    boolean existsByDoctorIdAndSlotDateAndStartTime(Long doctorId, LocalDate slotDate, LocalTime startTime);

    // Schedule retrieval: all slots for a doctor on a given date, ordered chronologically.
    List<AppointmentSlot> findAllByDoctorIdAndSlotDateOrderByStartTime(Long doctorId, LocalDate slotDate);
}
