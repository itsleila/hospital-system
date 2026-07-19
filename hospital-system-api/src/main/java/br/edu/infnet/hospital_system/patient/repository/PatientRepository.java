package br.edu.infnet.hospital_system.patient.repository;

import java.util.Optional;

import br.edu.infnet.hospital_system.patient.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface PatientRepository extends JpaRepository<Patient, Long>, RevisionRepository<Patient, Long, Integer> {

    boolean existsByCpf(String cpf);

    Optional<Patient> findByCpf(String cpf);
}