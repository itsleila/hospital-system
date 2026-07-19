package br.edu.infnet.hospital_system.doctor.repository;

import java.util.Optional;

import br.edu.infnet.hospital_system.doctor.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long>, RevisionRepository<Doctor, Long, Integer> {

    boolean existsByCrmIgnoreCase(String crm);

    Optional<Doctor> findByCrmIgnoreCase(String crm);
}