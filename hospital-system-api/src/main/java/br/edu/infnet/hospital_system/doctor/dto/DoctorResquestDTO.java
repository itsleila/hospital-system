package br.edu.infnet.hospital_system.doctor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResquestDTO {
    String name;
    String surname;
    String email;
    String specialty;
    String CRM;
}
