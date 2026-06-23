package Backend.ClinicBookPatientSyetem.repository;

import Backend.ClinicBookPatientSyetem.model.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {

    boolean existsByDoctorIdAndSlotDate(Long doctorId, LocalDate slotDate);

    List<AppointmentSlot> findAllByDoctorIdAndSlotDateOrderByStartTime(Long doctorId, LocalDate slotDate);
}
