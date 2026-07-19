package br.edu.infnet.hospital_system.doctor.model;

import br.edu.infnet.hospital_system.shared.persistence.AuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Audited
@Table(name = "doctors", uniqueConstraints = {
                @UniqueConstraint(name = "uk_doctor_crm", columnNames = "crm")}
)
public class Doctor extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String surname;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 30)
    private String crm;

    @Column(nullable = false, length = 100)
    private String specialty;
}
