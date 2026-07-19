package br.edu.infnet.hospital_system.appointment.model;

import java.time.LocalDateTime;

import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.patient.model.Patient;
import br.edu.infnet.hospital_system.shared.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited(withModifiedFlag = true)
@Table(name = "appointments", uniqueConstraints = {
                @UniqueConstraint(name = "uk_appointment_doctor_datetime", columnNames = {"doctor_id", "date_time"})}
)
public class Appointment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;
}