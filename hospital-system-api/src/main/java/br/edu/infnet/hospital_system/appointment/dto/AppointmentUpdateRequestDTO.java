package br.edu.infnet.hospital_system.appointment.dto;

import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AppointmentUpdateRequestDTO {

    private Long doctorId;
    private LocalDateTime dateTime;
    private AppointmentStatus status;
}
