package br.edu.infnet.hospital_system.appointment.dto;

import java.time.LocalDateTime;

import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRequestDTO {
    private LocalDateTime dateTime;
    private AppointmentStatus status;

    private Long doctorId;
    private Long patientId;
}
