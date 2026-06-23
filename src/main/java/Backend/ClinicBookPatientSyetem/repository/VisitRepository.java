package Backend.ClinicBookPatientSyetem.repository;

import Backend.ClinicBookPatientSyetem.model.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    boolean existsByAppointmentId(Long appointmentId);

    Optional<Visit> findByAppointmentId(Long appointmentId);
}
