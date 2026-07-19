package br.edu.infnet.hospital_system.appointment;

import static br.edu.infnet.hospital_system.support.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import br.edu.infnet.hospital_system.HospitalSystemApplication;
import br.edu.infnet.hospital_system.PostgreSQLIntegrationTest;
import br.edu.infnet.hospital_system.appointment.model.Appointment;
import br.edu.infnet.hospital_system.appointment.repository.AppointmentRepository;
import br.edu.infnet.hospital_system.config.TestcontainersConfiguration;
import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.doctor.repository.DoctorRepository;
import br.edu.infnet.hospital_system.patient.model.Patient;
import br.edu.infnet.hospital_system.patient.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(classes = HospitalSystemApplication.class)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AppointmentHistoryTest extends PostgreSQLIntegrationTest {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TransactionTemplate transactionTemplate;

    public AppointmentHistoryTest(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
            DoctorRepository doctorRepository, PlatformTransactionManager transactionManager) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Test
    @DisplayName("Deve registrar revisões de inserção, atualização e exclusão de agendamento")
    void shouldRegisterInsertUpdateAndDeleteRevisions() {
        LocalDateTime originalDateTime = LocalDateTime.of(2026, 9, 10, 10, 0);
        LocalDateTime updatedDateTime = LocalDateTime.of(2026, 9, 10, 11, 0);

        Long appointmentId = transactionTemplate.execute(status -> {
            Patient patient = patientRepository.save(createPatient("77788899900"));
            Doctor doctor = doctorRepository.save(createDoctor("CRM-SP-6000"));

            Appointment appointment = createAppointment(patient, doctor, originalDateTime);

            return appointmentRepository.save(appointment).getId();
        });

        transactionTemplate.executeWithoutResult(status -> {
            Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
            appointment.setDateTime(updatedDateTime);
        });

        transactionTemplate.executeWithoutResult(status -> {
            Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow();
            appointmentRepository.delete(appointment);
        });

        List<Revision<Integer, Appointment>> revisions = transactionTemplate.execute(status -> appointmentRepository
                .findRevisions(appointmentId)
                .stream()
                .toList());

        assertThat(revisions).hasSize(3);

        assertThat(revisions.get(0).getMetadata().getRevisionType()).isEqualTo(RevisionMetadata.RevisionType.INSERT);
        assertThat(revisions.get(1).getMetadata().getRevisionType()).isEqualTo(RevisionMetadata.RevisionType.UPDATE);
        assertThat(revisions.get(2).getMetadata().getRevisionType()).isEqualTo(RevisionMetadata.RevisionType.DELETE);

        assertThat(revisions.get(0).getEntity().getDateTime()).isEqualTo(originalDateTime);
        assertThat(revisions.get(1).getEntity().getDateTime()).isEqualTo(updatedDateTime);
        assertThat(revisions.get(2).getEntity().getDateTime()).isEqualTo(updatedDateTime);
    }
}