package br.edu.infnet.hospital_system.appointment.dto;

import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    private Long id;
    private LocalDateTime dateTime;
    private AppointmentStatus status;

    private String doctorName;
    private String patientName;
}
