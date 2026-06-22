package Backend.ClinicBookPatientSyetem.model.entity;

import Backend.ClinicBookPatientSyetem.model.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private AppointmentSlot slot;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AppointmentStatus status;

    @Column(name = "booked_at", nullable = false)
    private LocalDateTime bookedAt;

    // Self-referencing FK: points to the new appointment created during rescheduling.
    // The original row is never deleted — it is marked RESCHEDULED and this FK is populated.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rescheduled_to", nullable = true)
    private Appointment rescheduledTo;
}
