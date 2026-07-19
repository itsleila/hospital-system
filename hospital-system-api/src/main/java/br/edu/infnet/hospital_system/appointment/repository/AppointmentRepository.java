package br.edu.infnet.hospital_system.appointment.repository;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.infnet.hospital_system.appointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, RevisionRepository<Appointment, Long, Integer> {

    boolean existsByDoctor_IdAndDateTime(Long doctorId, LocalDateTime dateTime);

    boolean existsByDoctor_IdAndDateTimeAndIdNot(
            Long doctorId,
            LocalDateTime dateTime,
            Long appointmentId
    );

    boolean existsByPatient_Id(Long patientId);

    boolean existsByDoctor_Id(Long doctorId);

    List<Appointment> findByPatient_Id(Long patientId);

    List<Appointment> findByPatient_Cpf(String cpf);

    List<Appointment> findByDoctor_Id(Long doctorId);

    List<Appointment>
    findByDateTimeGreaterThanEqualAndDateTimeLessThan(LocalDateTime start, LocalDateTime end);
}
