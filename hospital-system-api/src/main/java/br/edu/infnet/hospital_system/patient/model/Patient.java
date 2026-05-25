package br.edu.infnet.hospital_system.patient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    private Long id;
    private String name;
    private String surname;
    private String cpf;
    private Date birthDate;
    private String gender;
    private Integer phoneNumber;


}
