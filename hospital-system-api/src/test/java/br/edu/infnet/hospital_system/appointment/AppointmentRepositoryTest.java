package br.edu.infnet.hospital_system.appointment;

import static br.edu.infnet.hospital_system.support.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import br.edu.infnet.hospital_system.HospitalSystemApplication;
import br.edu.infnet.hospital_system.PostgreSQLIntegrationTest;
import br.edu.infnet.hospital_system.appointment.model.Appointment;
import br.edu.infnet.hospital_system.appointment.model.AppointmentStatus;
import br.edu.infnet.hospital_system.appointment.repository.AppointmentRepository;
import br.edu.infnet.hospital_system.config.TestcontainersConfiguration;
import br.edu.infnet.hospital_system.doctor.model.Doctor;
import br.edu.infnet.hospital_system.doctor.repository.DoctorRepository;
import br.edu.infnet.hospital_system.patient.model.Patient;
import br.edu.infnet.hospital_system.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(classes = HospitalSystemApplication.class)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class AppointmentRepositoryTest extends PostgreSQLIntegrationTest {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    private Patient patient;
    private Doctor doctor;

    public AppointmentRepositoryTest(AppointmentRepository appointmentRepository, PatientRepository patientRepository,
            DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @BeforeEach
    void setUp() {
        patient = patientRepository.save(createPatient("55566677788"));

        doctor = doctorRepository.save(createDoctor("CRM-SP-5000"));
    }

    @Test
    @DisplayName("Deve persistir agendamento com relacionamentos")
    void shouldPersistAppointmentWithRelationships() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 10, 14, 0);

        Appointment appointment = appointmentRepository.saveAndFlush(createAppointment(patient, doctor, dateTime));
        Appointment foundAppointment = appointmentRepository.findById(appointment.getId()).orElseThrow();

        assertThat(foundAppointment.getId()).isNotNull();
        assertThat(foundAppointment.getPatient().getId()).isEqualTo(patient.getId());
        assertThat(foundAppointment.getDoctor().getId()).isEqualTo(doctor.getId());
        assertThat(foundAppointment.getDateTime()).isEqualTo(dateTime);
        assertThat(foundAppointment.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    @DisplayName("Deve verificar conflito de agenda do médico")
    void shouldVerifyDoctorScheduleConflict() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 11, 10, 0);

        appointmentRepository.saveAndFlush(createAppointment(patient, doctor, dateTime));

        boolean doctorUnavailable = appointmentRepository.existsByDoctor_IdAndDateTime(doctor.getId(), dateTime);
        assertThat(doctorUnavailable).isTrue();
    }

    @Test
    @DisplayName("Deve rejeitar dois agendamentos para o mesmo médico e horário")
    void shouldRejectTwoAppointmentsForSameDoctorAndTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 12, 9, 30);
        appointmentRepository.saveAndFlush(createAppointment(patient, doctor, dateTime));

        Patient secondPatient = patientRepository.save(createPatient("66677788899"));
        Appointment conflictingAppointment = createAppointment(secondPatient, doctor, dateTime);

        assertThatThrownBy(() -> appointmentRepository.saveAndFlush(conflictingAppointment))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Deve encontrar agendamentos pelo ID do paciente")
    void shouldFindAppointmentsByPatientId() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 13, 11, 0);

        appointmentRepository.saveAndFlush(createAppointment(patient, doctor, dateTime));

        List<Appointment> appointments = appointmentRepository.findByPatient_Id(patient.getId());

        assertThat(appointments).hasSize(1);
        assertThat(appointments.getFirst().getPatient().getId()).isEqualTo(patient.getId());
    }

    @Test
    @DisplayName("Deve encontrar agendamentos pelo CPF do paciente")
    void shouldFindAppointmentsByPatientCpf() {
        appointmentRepository.saveAndFlush(createAppointment(patient, doctor, LocalDateTime.of(2026, 8, 14, 12, 0)));

        List<Appointment> appointments = appointmentRepository.findByPatient_Cpf(patient.getCpf());

        assertThat(appointments).hasSize(1);
        assertThat(appointments.getFirst().getPatient().getCpf()).isEqualTo(patient.getCpf());
    }

    @Test
    @DisplayName("Deve encontrar agendamentos pelo ID do médico")
    void shouldFindAppointmentsByDoctorId() {
        appointmentRepository.saveAndFlush(createAppointment(patient, doctor, LocalDateTime.of(2026, 8, 15, 13, 0)));
        List<Appointment> appointments = appointmentRepository.findByDoctor_Id(doctor.getId());

        assertThat(appointments).hasSize(1);
        assertThat(appointments.getFirst().getDoctor().getId()).isEqualTo(doctor.getId());
    }

    @Test
    @DisplayName("Deve encontrar agendamentos dentro de um intervalo de dias")
    void shouldFindAppointmentsWithinDayRange() {
        LocalDate selectedDate = LocalDate.of(2026, 8, 16);

        appointmentRepository.save(createAppointment(patient, doctor, selectedDate.atTime(8, 0)));
        Doctor secondDoctor = doctorRepository.save(createDoctor("CRM-SP-5001"));
        appointmentRepository.save(createAppointment(patient, secondDoctor, selectedDate.atTime(16, 0)));

        Doctor thirdDoctor = doctorRepository.save(createDoctor("CRM-SP-5002"));
        appointmentRepository
                .saveAndFlush(createAppointment(patient, thirdDoctor, selectedDate.plusDays(1).atTime(8, 0)));

        LocalDateTime start = selectedDate.atStartOfDay();
        LocalDateTime end = selectedDate.plusDays(1).atStartOfDay();

        List<Appointment> appointments = appointmentRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThan(start,
                end);

        assertThat(appointments).hasSize(2);
        assertThat(appointments).allMatch(appointment -> appointment.getDateTime().toLocalDate().equals(selectedDate));
    }

    @Test
    @DisplayName("Deve ignorar o agendamento atual ao verificar conflito de atualização")
    void shouldIgnoreCurrentAppointmentWhenCheckingUpdate() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 17, 15, 0);

        Appointment appointment = appointmentRepository.saveAndFlush(createAppointment(patient, doctor, dateTime));
        boolean conflict = appointmentRepository.existsByDoctor_IdAndDateTimeAndIdNot(doctor.getId(), dateTime,
                appointment.getId());

        assertThat(conflict).isFalse();
    }

}