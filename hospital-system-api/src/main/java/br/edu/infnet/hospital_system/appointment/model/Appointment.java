package br.edu.infnet.hospital_system.appointment.model;

import java.time.LocalDateTime;

import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.patient.model.Patient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    private Long id;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
    private Doctor doctor;
    private Patient patient;
}