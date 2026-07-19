package br.edu.infnet.hospital_system.patient.model;

import br.edu.infnet.hospital_system.shared.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@Table(name = "patients", uniqueConstraints = {
        @UniqueConstraint(name = "uk_patient_cpf", columnNames = "cpf")}
)
public class Patient extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String surname;

    @Column(nullable = false, length = 14)
    private String cpf;

    @Column(nullable = false)
    private LocalDate birthdate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Gender gender;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phonenumber;


}
