package br.edu.infnet.hospital_system.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientRequestDTO {
    private String name;
    private String surname;
    private String cpf;
    private LocalDate birthdate;
    private String gender;
    private String phonenumber;
}
