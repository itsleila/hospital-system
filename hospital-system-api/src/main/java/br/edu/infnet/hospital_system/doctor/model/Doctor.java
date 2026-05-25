package br.edu.infnet.hospital_system.doctor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
    Long id;
    String name;
    String surname;
    String email;
    String specialty;
    String CRM;

}
