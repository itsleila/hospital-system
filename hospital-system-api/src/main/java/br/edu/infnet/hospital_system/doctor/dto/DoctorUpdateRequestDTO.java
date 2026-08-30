package br.edu.infnet.hospital_system.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorUpdateRequestDTO {

    private String name;
    private String surname;
    private String email;
    private String specialty;
}
