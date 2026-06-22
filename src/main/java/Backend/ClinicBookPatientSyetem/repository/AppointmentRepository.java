package Backend.ClinicBookPatientSyetem.repository;

import Backend.ClinicBookPatientSyetem.model.entity.Appointment;
import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Phase 3 — schedule view: is this slot currently held by a BOOKED appointment?
    boolean existsBySlotIdAndStatus(Long slotId, AppointmentStatus status);
}
