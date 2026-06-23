package Backend.ClinicBookPatientSyetem.repository;

import Backend.ClinicBookPatientSyetem.model.entity.Appointment;
import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Phase 3 — schedule view: is this slot currently held by a BOOKED appointment?
    boolean existsBySlotIdAndStatus(Long slotId, AppointmentStatus status);

    // Phase 4 — booking rule 2: does this patient already have a BOOKED appointment
    // with the same doctor on the same calendar day?
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN TRUE ELSE FALSE END FROM Appointment a " +
           "WHERE a.patient.id = :patientId " +
           "AND a.slot.doctor.id = :doctorId " +
           "AND a.slot.slotDate = :date " +
           "AND a.status = 'BOOKED'")
    boolean existsActiveBookingForPatientDoctorDay(@Param("patientId") Long patientId,
                                                   @Param("doctorId") Long doctorId,
                                                   @Param("date") LocalDate date);

    // Phase 5 — patient history: all appointments ordered most-recent first.
    List<Appointment> findAllByPatientIdOrderByBookedAtDesc(Long patientId);
}

