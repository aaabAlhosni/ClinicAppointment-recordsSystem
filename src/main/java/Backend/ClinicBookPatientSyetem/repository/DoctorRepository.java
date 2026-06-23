package Backend.ClinicBookPatientSyetem.repository;

import Backend.ClinicBookPatientSyetem.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    boolean existsByNameAndSpecialty(String name, String specialty);
}
