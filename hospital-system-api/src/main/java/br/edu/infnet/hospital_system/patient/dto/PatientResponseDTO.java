package br.edu.infnet.hospital_system.patient.dto;

import br.edu.infnet.hospital_system.patient.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientResponseDTO {
    private Long id;
    private String name;
    private String surname;
    private String cpf;
    private LocalDate birthdate;
    private Gender gender;
    private String phonenumber;
}
