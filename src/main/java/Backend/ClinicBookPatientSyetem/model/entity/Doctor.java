package Backend.ClinicBookPatientSyetem.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "doctor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "specialty"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "specialty", nullable = false)
    private String specialty;

    @Column(name = "work_start", nullable = false)
    private LocalTime workStart;

    @Column(name = "work_end", nullable = false)
    private LocalTime workEnd;
}
